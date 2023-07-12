package de.focusshift.zeiterfassung.absence;

import de.focusshift.zeiterfassung.tenancy.tenant.TenantContextHolder;
import de.focusshift.zeiterfassung.tenancy.tenant.TenantId;
import de.focusshift.zeiterfassung.user.UserId;
import de.focusshift.zeiterfassung.user.UserSettingsProvider;
import de.focusshift.zeiterfassung.usermanagement.User;
import de.focusshift.zeiterfassung.usermanagement.UserLocalId;
import de.focusshift.zeiterfassung.usermanagement.UserManagementService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Service
class AbsenceServiceImpl implements AbsenceService {

    private final AbsenceRepository absenceRepository;
    private final UserSettingsProvider userSettingsProvider;
    private final TenantContextHolder tenantContextHolder;
    private final UserManagementService userManagementService;

    AbsenceServiceImpl(AbsenceRepository absenceRepository, UserSettingsProvider userSettingsProvider, TenantContextHolder tenantContextHolder, UserManagementService userManagementService) {
        this.absenceRepository = absenceRepository;
        this.userSettingsProvider = userSettingsProvider;
        this.tenantContextHolder = tenantContextHolder;
        this.userManagementService = userManagementService;
    }

    @Override
    public Map<LocalDate, List<Absence>> findAllAbsences(UserId userId, Instant from, Instant toExclusive) {

        final ZoneId zoneId = userSettingsProvider.zoneId();
        final String tenantId = tenantContextHolder.getCurrentTenantId().orElse(new TenantId("")).tenantId();

        final List<Absence> absences =
            absenceRepository.findAllByTenantIdAndUserIdAndStartDateGreaterThanEqualAndEndDateLessThan(tenantId, userId.value(), from, toExclusive)
                .stream()
                .map(entity -> toAbsence(entity, zoneId))
                .toList();

        final Map<LocalDate, List<Absence>> absencesByDate = new HashMap<>();

        for (Absence absence : absences) {
            final LocalDate start = LocalDate.ofInstant(absence.startDate().toInstant(), zoneId);
            final LocalDate endExclusive = LocalDate.ofInstant(absence.endDate().toInstant(), zoneId).plusDays(1);
            doFromUntil(start, endExclusive, date -> absencesByDate.computeIfAbsent(date, unused -> new ArrayList<>()).add(absence));
        }

        // add empty lists for dates without absences
        final LocalDate fromDate = LocalDate.ofInstant(from, zoneId);
        final LocalDate toDateExclusive = LocalDate.ofInstant(toExclusive, zoneId);
        doFromUntil(fromDate, toDateExclusive, date -> absencesByDate.computeIfAbsent(date, unused -> new ArrayList<>()));

        return absencesByDate;
    }

    @Override
    public Map<UserLocalId, List<Absence>> getAbsencesByUserIds(List<UserLocalId> userLocalIds, LocalDate from, LocalDate toExclusive) {
        final ZoneId zoneId = userSettingsProvider.zoneId();
        final String tenantId = tenantContextHolder.getCurrentTenantId().orElse(new TenantId("")).tenantId();

        final List<User> users = userManagementService.findAllUsersByLocalIds(userLocalIds);

        final List<String> userIdValues = users
            .stream()
            .map(User::id)
            .map(UserId::value)
            .toList();

        final Map<UserId, UserLocalId> userLocalIdById = users.stream().collect(toMap(User::id, User::localId));

        final Instant fromInstant = Instant.from(from.atStartOfDay().atZone(zoneId));
        final Instant toInstant = Instant.from(toExclusive.atStartOfDay().atZone(zoneId));
        return absenceRepository.findAllByTenantIdAndUserIdInAndStartDateGreaterThanEqualAndEndDateLessThan(tenantId, userIdValues, fromInstant, toInstant)
            .stream()
            .map(absenceWriteEntity -> toAbsence(absenceWriteEntity, zoneId))
            .collect(groupingBy(absence -> userLocalIdById.get(absence.userId())));
    }

    @Override
    public Map<UserLocalId, List<Absence>> getAbsencesForAllUsers(LocalDate from, LocalDate toExclusive) {
        final ZoneId zoneId = userSettingsProvider.zoneId();
        final String tenantId = tenantContextHolder.getCurrentTenantId().orElse(new TenantId("")).tenantId();

        final Instant fromInstant = Instant.from(from.atStartOfDay().atZone(zoneId));
        final Instant toInstant = Instant.from(toExclusive.atStartOfDay().atZone(zoneId));

        final Map<UserId, List<Absence>> absencedByUserId = absenceRepository.findAllByTenantIdAndStartDateGreaterThanEqualAndEndDateLessThan(tenantId, fromInstant, toInstant)
            .stream()
            .map(entity -> toAbsence(entity, zoneId))
            .collect(groupingBy(Absence::userId));

        final Map<UserId, User> userIdUserMap = userManagementService.findAllUsersByIds(absencedByUserId.keySet())
            .stream().collect(toMap(User::id, identity()));

        return absencedByUserId.entrySet().stream()
            .collect(
                toMap(userIdListEntry -> userIdUserMap.get(userIdListEntry.getKey()).localId(), Map.Entry::getValue));
    }

    @Override
    public List<Absence> getAbsencesByUserId(UserId userId, LocalDate from, LocalDate toExclusive) {

        final ZoneId zoneId = userSettingsProvider.zoneId();
        final String tenantId = tenantContextHolder.getCurrentTenantId().orElse(new TenantId("")).tenantId();

        final Instant fromInstant = Instant.from(from.atStartOfDay().atZone(zoneId));
        final Instant toInstant = Instant.from(toExclusive.atStartOfDay().atZone(zoneId));
        return absenceRepository.findAllByTenantIdAndUserIdAndStartDateGreaterThanEqualAndEndDateLessThan(tenantId, userId.value(), fromInstant, toInstant)
            .stream()
            .map(entity -> toAbsence(entity, zoneId))
            .toList();
    }

    private static void doFromUntil(LocalDate from, LocalDate toExclusive, Consumer<LocalDate> consumer) {
        LocalDate pivot = from;
        while (pivot.isBefore(toExclusive)) {
            consumer.accept(pivot);
            pivot = pivot.plusDays(1);
        }
    }

    private static Absence toAbsence(AbsenceWriteEntity entity, ZoneId zoneId) {
        return new Absence(
            new UserId(entity.getUserId()),
            entity.getStartDate().atZone(zoneId),
            entity.getEndDate().atZone(zoneId),
            entity.getDayLength(),
            entity.getType(),
            entity.getColor()
        );
    }
}