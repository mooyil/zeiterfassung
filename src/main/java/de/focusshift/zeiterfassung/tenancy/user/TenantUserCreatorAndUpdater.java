package de.focusshift.zeiterfassung.tenancy.user;

import de.focusshift.zeiterfassung.security.SecurityRole;
import de.focusshift.zeiterfassung.user.UserId;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

@Component
class TenantUserCreatorAndUpdater {

    private final TenantUserService tenantUserService;

    TenantUserCreatorAndUpdater(TenantUserService tenantUserService) {
        this.tenantUserService = tenantUserService;
    }

    @EventListener
    public void handle(InteractiveAuthenticationSuccessEvent interactiveAuthenticationSuccessEvent) {
        if (interactiveAuthenticationSuccessEvent.getAuthentication().getPrincipal() instanceof final DefaultOidcUser oidcUser) {
            final EMailAddress eMailAddress = new EMailAddress(oidcUser.getEmail());

            final Set<SecurityRole> authorities = oidcUser.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(startsWith("ROLE_").and(not("ROLE_USER"::equals)))
                .map(s -> s.substring("ROLE_".length()))
                .map(SecurityRole::valueOf)
                .collect(toSet());

            final Optional<TenantUser> maybeUser = tenantUserService.findById(new UserId(oidcUser.getSubject()));
            if (maybeUser.isEmpty()) {
                tenantUserService.createNewUser(oidcUser.getSubject(), oidcUser.getGivenName(), oidcUser.getFamilyName(), eMailAddress, authorities);
            } else {
                final TenantUser user = maybeUser.get();
                final TenantUser tenantUser = new TenantUser(user.id(), user.localId(), oidcUser.getGivenName(), oidcUser.getFamilyName(), eMailAddress, authorities);
                tenantUserService.updateUser(tenantUser);
            }
        }
    }

    private static Predicate<String> startsWith(String prefix) {
        return s -> s.startsWith(prefix);
    }
}
