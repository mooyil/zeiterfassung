package de.focusshift.zeiterfassung.report;

import de.focusshift.zeiterfassung.user.DateFormatter;
import de.focusshift.zeiterfassung.user.UserId;
import de.focusshift.zeiterfassung.usermanagement.User;
import de.focusshift.zeiterfassung.usermanagement.UserLocalId;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Component
class ReportControllerHelper {

    private final ReportPermissionService reportPermissionService;
    private final DateFormatter dateFormatter;

    ReportControllerHelper(ReportPermissionService reportPermissionService, DateFormatter dateFormatter) {
        this.reportPermissionService = reportPermissionService;
        this.dateFormatter = dateFormatter;
    }

    UserId principalToUserId(OidcUser principal) {
        return new UserId(principal.getUserInfo().getSubject());
    }

    void addUserFilterModelAttributes(Model model, boolean allUsersSelected, List<UserLocalId> selectedUserLocalIds, String userReportFilterUrl) {

        final List<User> permittedUsers = reportPermissionService.findAllPermittedUsersForCurrentUser();
        if (permittedUsers.size() > 1) {
            final List<SelectableUserDto> selectableUserDtos = permittedUsers
                .stream()
                .map(user -> userToSelectableUserDto(user, selectedUserLocalIds.contains(user.localId())))
                .toList();

            model.addAttribute("users", selectableUserDtos);
            model.addAttribute("selectedUserIds", selectedUserLocalIds.stream().map(UserLocalId::value).toList());
            model.addAttribute("allUsersSelected", allUsersSelected);
            model.addAttribute("userReportFilterUrl", userReportFilterUrl);
        }
    }

    private static SelectableUserDto userToSelectableUserDto(User user, boolean selected) {
        return new SelectableUserDto(user.localId().value(), user.givenName() + " " + user.familyName(), selected);
    }

    GraphWeekDto toGraphWeekDto(ReportWeek reportWeek, Month monthPivot) {

        final List<GraphDayDto> dayReports = reportWeek.reportDays()
            .stream()
            .map(reportDay -> toUserReportDayReportDto(reportDay, !reportDay.date().getMonth().equals(monthPivot)))
            .toList();

        final String yearMonthWeek = dateFormatter.formatYearMonthWeek(reportWeek.firstDateOfWeek());

        final double maxHoursWorked = dayReports.stream()
            .map(GraphDayDto::hoursWorked)
            .mapToDouble(value -> value)
            .max().orElse(0.0);

        final double hoursWorkedAverageADay = reportWeek.averageDayWorkDuration().hoursDoubleValue();

        return new GraphWeekDto(yearMonthWeek, dayReports, maxHoursWorked, hoursWorkedAverageADay);
    }

    private GraphDayDto toUserReportDayReportDto(ReportDay reportDay, boolean differentMonth) {

        final String dayOfWeekNarrow = dateFormatter.formatDayOfWeekNarrow(reportDay.date().getDayOfWeek());
        final String dayOfWeekFull = dateFormatter.formatDayOfWeekFull(reportDay.date().getDayOfWeek());
        final String dateString = dateFormatter.formatDate(reportDay.date());
        final double hoursWorked = reportDay.workDuration().hoursDoubleValue();
        final double hoursWorkedShould = reportDay.plannedWorkingHours().hoursDoubleValue();

        return new GraphDayDto(differentMonth, dayOfWeekNarrow, dayOfWeekFull, dateString, hoursWorked, hoursWorkedShould);
    }

    DetailWeekDto toDetailWeekDto(ReportWeek reportWeek, Month monthPivot) {

        final List<DetailDayDto> dayReports = reportWeek.reportDays()
            .stream()
            .map(reportDay -> toDetailDayReportDto(reportDay, !reportDay.date().getMonth().equals(monthPivot)))
            .toList();

        final LocalDate first = reportWeek.firstDateOfWeek();
        final LocalDate last = reportWeek.lastDateOfWeek();
        final int calendarWeek = first.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        final ZonedDateTime firstOfWeek = ZonedDateTime.of(first, LocalTime.MIN, ZoneId.systemDefault());
        final ZonedDateTime lastOfWeek = ZonedDateTime.of(last, LocalTime.MIN, ZoneId.systemDefault());

        return new DetailWeekDto(Date.from(firstOfWeek.toInstant()), Date.from(lastOfWeek.toInstant()), calendarWeek, dayReports);
    }

    String createUrl(String prefix, boolean allUsersSelected, List<UserLocalId> selectedUserLocalIds) {
        String url = prefix;

        if (allUsersSelected || !selectedUserLocalIds.isEmpty()) {
            url += "?";
        }

        if (allUsersSelected) {
            url += "everyone=";
        }

        final String usersParam = selectedUserLocalIds.stream()
            .map(UserLocalId::value)
            .map(id -> "user=" + id)
            .collect(joining("&"));

        if (!usersParam.isEmpty()) {
            if (allUsersSelected) {
                url += "&";
            }
            url += usersParam;
        }

        return url;
    }

    private DetailDayDto toDetailDayReportDto(ReportDay reportDay, boolean differentMonth) {

        final String dayOfWeekNarrow = dateFormatter.formatDayOfWeekNarrow(reportDay.date().getDayOfWeek());
        final String dayOfWeekFull = dateFormatter.formatDayOfWeekFull(reportDay.date().getDayOfWeek());
        final String dateString = dateFormatter.formatDate(reportDay.date());
        final double hoursWorked = reportDay.workDuration().hoursDoubleValue();
        final List<DetailDayEntryDto> dayEntryDtos = reportDay.reportDayEntries().stream().map(this::toDetailDayEntryDto).toList();

        return new DetailDayDto(differentMonth, dayOfWeekNarrow, dayOfWeekFull, dateString, hoursWorked, dayEntryDtos);
    }

    private DetailDayEntryDto toDetailDayEntryDto(ReportDayEntry reportDayEntry) {
        return new DetailDayEntryDto(reportDayEntry.user().fullName(), reportDayEntry.comment(), Date.from(reportDayEntry.start().toInstant()), Date.from(reportDayEntry.end().toInstant()));
    }
}