package de.focusshift.zeiterfassung.usermanagement;

import de.focusshift.zeiterfassung.timeentry.PlannedWorkingHours;
import de.focusshift.zeiterfassung.user.UserDateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkTimeServiceImplTest {

    private WorkTimeServiceImpl sut;

    @Mock
    private WorkingTimeRepository workingTimeRepository;

    @Mock
    private UserDateService userDateService;

    @Mock
    private UserManagementService userManagementService;

    @BeforeEach
    void setUp() {
        sut = new WorkTimeServiceImpl(workingTimeRepository, userDateService, userManagementService);
    }

    @Test
    void ensureWorkingTimeByUser() {

        final WorkingTimeEntity entity = new WorkingTimeEntity();
        entity.setUserId(42L);
        entity.setMonday("PT1H");
        entity.setTuesday("PT2H");
        entity.setWednesday("PT3H");
        entity.setThursday("PT4H");
        entity.setFriday("PT5H");
        entity.setSaturday("PT6H");
        entity.setSunday("PT7H");

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.of(entity));

        final WorkingTime actual = sut.getWorkingTimeByUser(new UserLocalId(42L));

        assertThat(actual.getUserId()).isEqualTo(new UserLocalId(42L));
        assertThat(actual.getMonday()).hasValue(WorkDay.monday(Duration.ofHours(1)));
        assertThat(actual.getTuesday()).hasValue(WorkDay.tuesday(Duration.ofHours(2)));
        assertThat(actual.getWednesday()).hasValue(WorkDay.wednesday(Duration.ofHours(3)));
        assertThat(actual.getThursday()).hasValue(WorkDay.thursday(Duration.ofHours(4)));
        assertThat(actual.getFriday()).hasValue(WorkDay.friday(Duration.ofHours(5)));
        assertThat(actual.getSaturday()).hasValue(WorkDay.saturday(Duration.ofHours(6)));
        assertThat(actual.getSunday()).hasValue(WorkDay.sunday(Duration.ofHours(7)));
    }

    @Test
    void ensureWorkingTimeByUserReturnsDefault() {

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.empty());

        final WorkingTime actual = sut.getWorkingTimeByUser(new UserLocalId(42L));

        assertThat(actual.getUserId()).isEqualTo(new UserLocalId(42L));
        assertThat(actual.getMonday()).hasValue(WorkDay.monday(Duration.ofHours(8)));
        assertThat(actual.getTuesday()).hasValue(WorkDay.tuesday(Duration.ofHours(8)));
        assertThat(actual.getWednesday()).hasValue(WorkDay.wednesday(Duration.ofHours(8)));
        assertThat(actual.getThursday()).hasValue(WorkDay.thursday(Duration.ofHours(8)));
        assertThat(actual.getFriday()).hasValue(WorkDay.friday(Duration.ofHours(8)));
        assertThat(actual.getSaturday()).hasValue(WorkDay.saturday(Duration.ZERO));
        assertThat(actual.getSunday()).hasValue(WorkDay.sunday(Duration.ZERO));
    }

    @Test
    void ensureGetWorkingTimeByUsers() {

        final WorkingTimeEntity entity_1 = new WorkingTimeEntity();
        entity_1.setUserId(1L);
        entity_1.setMonday("PT1H");
        entity_1.setTuesday("PT2H");
        entity_1.setWednesday("PT3H");
        entity_1.setThursday("PT4H");
        entity_1.setFriday("PT5H");
        entity_1.setSaturday("PT6H");
        entity_1.setSunday("PT7H");

        final WorkingTimeEntity entity_2 = new WorkingTimeEntity();
        entity_2.setUserId(2L);
        entity_2.setMonday("PT7H");
        entity_2.setTuesday("PT6H");
        entity_2.setWednesday("PT5H");
        entity_2.setThursday("PT4H");
        entity_2.setFriday("PT3H");
        entity_2.setSaturday("PT2H");
        entity_2.setSunday("PT1H");

        when(workingTimeRepository.findAllByUserIdIsIn(List.of(1L, 2L))).thenReturn(List.of(entity_1, entity_2));

        final Map<UserLocalId, WorkingTime> actual = sut.getWorkingTimeByUsers(List.of(new UserLocalId(1L), new UserLocalId(2L)));

        assertThat(actual)
            .containsEntry(new UserLocalId(1L), WorkingTime.builder()
                .userId(new UserLocalId(1L))
                .monday(1)
                .tuesday(2)
                .wednesday(3)
                .thursday(4)
                .friday(5)
                .saturday(6)
                .sunday(8)
                .build()
            )
            .containsEntry(new UserLocalId(2L), WorkingTime.builder()
                .userId(new UserLocalId(2L))
                .monday(7)
                .tuesday(6)
                .wednesday(5)
                .thursday(4)
                .friday(4)
                .saturday(2)
                .sunday(1)
                .build()
            );
    }

    @Test
    void ensureGetWorkingTimeByUsersAddsDefaultWorkingTimeForUsersWithoutExplicitOne() {

        when(workingTimeRepository.findAllByUserIdIsIn(List.of(1L))).thenReturn(List.of());

        final Map<UserLocalId, WorkingTime> actual = sut.getWorkingTimeByUsers(List.of(new UserLocalId(1L)));

        assertThat(actual)
            .containsEntry(new UserLocalId(1L), WorkingTime.builder()
                .userId(new UserLocalId(1L))
                .monday(8)
                .tuesday(8)
                .wednesday(8)
                .thursday(8)
                .friday(8)
                .saturday(0)
                .sunday(0)
                .build()
            );
    }

    @Test
    void ensureGetAllWorkingTimeByUsers() {

        final WorkingTimeEntity entity_1 = new WorkingTimeEntity();
        entity_1.setUserId(1L);
        entity_1.setMonday("PT1H");
        entity_1.setTuesday("PT2H");
        entity_1.setWednesday("PT3H");
        entity_1.setThursday("PT4H");
        entity_1.setFriday("PT5H");
        entity_1.setSaturday("PT6H");
        entity_1.setSunday("PT7H");

        final WorkingTimeEntity entity_2 = new WorkingTimeEntity();
        entity_2.setUserId(2L);
        entity_2.setMonday("PT7H");
        entity_2.setTuesday("PT6H");
        entity_2.setWednesday("PT5H");
        entity_2.setThursday("PT4H");
        entity_2.setFriday("PT3H");
        entity_2.setSaturday("PT2H");
        entity_2.setSunday("PT1H");

        when(userManagementService.findAllUsers()).thenReturn(List.of(
            new User(null, new UserLocalId(1L), "", "", null, Set.of()),
            new User(null, new UserLocalId(2L), "", "", null, Set.of())
        ));

        when(workingTimeRepository.findAllByUserIdIsIn(List.of(1L, 2L))).thenReturn(List.of(entity_1, entity_2));

        final Map<UserLocalId, WorkingTime> actual = sut.getAllWorkingTimeByUsers();

        assertThat(actual)
            .containsEntry(new UserLocalId(1L), WorkingTime.builder()
                .userId(new UserLocalId(1L))
                .monday(1)
                .tuesday(2)
                .wednesday(3)
                .thursday(4)
                .friday(5)
                .saturday(6)
                .sunday(8)
                .build()
            )
            .containsEntry(new UserLocalId(2L), WorkingTime.builder()
                .userId(new UserLocalId(2L))
                .monday(7)
                .tuesday(6)
                .wednesday(5)
                .thursday(4)
                .friday(4)
                .saturday(2)
                .sunday(1)
                .build()
            );
    }

    @Test
    void ensureGetAllWorkingTimeByUsersAddsDefaultWorkingTimeForUsersWithoutExplicitOne() {

        when(userManagementService.findAllUsers())
            .thenReturn(List.of(new User(null, new UserLocalId(1L), "", "", null, Set.of())));

        when(workingTimeRepository.findAllByUserIdIsIn(List.of(1L))).thenReturn(List.of());

        final Map<UserLocalId, WorkingTime> actual = sut.getAllWorkingTimeByUsers();

        assertThat(actual)
            .containsEntry(new UserLocalId(1L), WorkingTime.builder()
                .userId(new UserLocalId(1L))
                .monday(8)
                .tuesday(8)
                .wednesday(8)
                .thursday(8)
                .friday(8)
                .saturday(0)
                .sunday(0)
                .build()
            );
    }

    @Test
    void ensureGetWorkingHoursByUserAndYearWeek() {

        final WorkingTimeEntity entity = new WorkingTimeEntity();
        entity.setUserId(42L);
        entity.setMonday("PT1H");
        entity.setTuesday("PT2H");
        entity.setWednesday("PT3H");
        entity.setThursday("PT4H");
        entity.setFriday("PT5H");
        entity.setSaturday("PT6H");
        entity.setSunday("PT7H");

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.of(entity));

        when(userDateService.firstDayOfWeek(Year.of(2023), 7))
            .thenReturn(LocalDate.of(2023, 2, 13));

        final Map<LocalDate, PlannedWorkingHours> actual = sut.getWorkingHoursByUserAndYearWeek(new UserLocalId(42L), Year.of(2023), 7);

        assertThat(actual)
            .containsEntry(LocalDate.of(2023, 2, 13), new PlannedWorkingHours(Duration.ofHours(1)))
            .containsEntry(LocalDate.of(2023, 2, 14), new PlannedWorkingHours(Duration.ofHours(2)))
            .containsEntry(LocalDate.of(2023, 2, 15), new PlannedWorkingHours(Duration.ofHours(3)))
            .containsEntry(LocalDate.of(2023, 2, 16), new PlannedWorkingHours(Duration.ofHours(4)))
            .containsEntry(LocalDate.of(2023, 2, 17), new PlannedWorkingHours(Duration.ofHours(5)))
            .containsEntry(LocalDate.of(2023, 2, 18), new PlannedWorkingHours(Duration.ofHours(6)))
            .containsEntry(LocalDate.of(2023, 2, 19), new PlannedWorkingHours(Duration.ofHours(7)));
    }

    @Test
    void ensureGetWorkingHoursByUserAndYearWeekUsesDefault() {

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.empty());

        when(userDateService.firstDayOfWeek(Year.of(2023), 7))
            .thenReturn(LocalDate.of(2023, 2, 13));

        final Map<LocalDate, PlannedWorkingHours> actual = sut.getWorkingHoursByUserAndYearWeek(new UserLocalId(42L), Year.of(2023), 7);

        assertThat(actual)
            .containsEntry(LocalDate.of(2023, 2, 13), PlannedWorkingHours.EIGHT)
            .containsEntry(LocalDate.of(2023, 2, 14), PlannedWorkingHours.EIGHT)
            .containsEntry(LocalDate.of(2023, 2, 15), PlannedWorkingHours.EIGHT)
            .containsEntry(LocalDate.of(2023, 2, 16), PlannedWorkingHours.EIGHT)
            .containsEntry(LocalDate.of(2023, 2, 17), PlannedWorkingHours.EIGHT)
            .containsEntry(LocalDate.of(2023, 2, 18), PlannedWorkingHours.ZERO)  // saturday
            .containsEntry(LocalDate.of(2023, 2, 19), PlannedWorkingHours.ZERO); // sunday
    }

    @Test
    void ensureUpdateWorkingTime() {

        final WorkingTimeEntity entity = new WorkingTimeEntity();
        entity.setUserId(42L);
        entity.setMonday("PT24H");
        entity.setTuesday("PT24H");
        entity.setWednesday("PT24H");
        entity.setThursday("PT24H");
        entity.setFriday("PT24H");
        entity.setSaturday("PT24H");
        entity.setSunday("PT24H");

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.of(entity));
        when(workingTimeRepository.save(any())).thenAnswer(returnsFirstArg());

        final WorkingTime workingTime = WorkingTime.builder()
            .userId(new UserLocalId(42L))
            .monday(BigDecimal.valueOf(1))
            .tuesday(BigDecimal.valueOf(2))
            .wednesday(BigDecimal.valueOf(3))
            .thursday(BigDecimal.valueOf(4))
            .friday(BigDecimal.valueOf(5))
            .saturday(BigDecimal.valueOf(6))
            .sunday(BigDecimal.valueOf(7))
            .build();

        final WorkingTime actual = sut.updateWorkingTime(workingTime);

        assertThat(actual).isNotSameAs(workingTime);
        assertThat(actual.getUserId()).isEqualTo(new UserLocalId(42L));
        assertThat(actual.getMonday()).hasValue(WorkDay.monday(Duration.ofHours(1)));
        assertThat(actual.getTuesday()).hasValue(WorkDay.tuesday(Duration.ofHours(2)));
        assertThat(actual.getWednesday()).hasValue(WorkDay.wednesday(Duration.ofHours(3)));
        assertThat(actual.getThursday()).hasValue(WorkDay.thursday(Duration.ofHours(4)));
        assertThat(actual.getFriday()).hasValue(WorkDay.friday(Duration.ofHours(5)));
        assertThat(actual.getSaturday()).hasValue(WorkDay.saturday(Duration.ofHours(6)));
        assertThat(actual.getSunday()).hasValue(WorkDay.sunday(Duration.ofHours(7)));

        final ArgumentCaptor<WorkingTimeEntity> captor = ArgumentCaptor.forClass(WorkingTimeEntity.class);
        verify(workingTimeRepository).save(captor.capture());

        final WorkingTimeEntity actualEntity = captor.getValue();
        assertThat(actualEntity.getUserId()).isEqualTo(42L);
        assertThat(actualEntity.getMonday()).isEqualTo("PT1H");
        assertThat(actualEntity.getTuesday()).isEqualTo("PT2H");
        assertThat(actualEntity.getWednesday()).isEqualTo("PT3H");
        assertThat(actualEntity.getThursday()).isEqualTo("PT4H");
        assertThat(actualEntity.getFriday()).isEqualTo("PT5H");
        assertThat(actualEntity.getSaturday()).isEqualTo("PT6H");
        assertThat(actualEntity.getSunday()).isEqualTo("PT7H");
    }

    @Test
    void ensureUpdateWorkingTimeWithNewItem() {

        when(workingTimeRepository.findByUserId(42L)).thenReturn(Optional.empty());
        when(workingTimeRepository.save(any())).thenAnswer(returnsFirstArg());

        final WorkingTime workingTime = WorkingTime.builder()
            .userId(new UserLocalId(42L))
            .monday(BigDecimal.valueOf(1))
            .tuesday(BigDecimal.valueOf(2))
            .wednesday(BigDecimal.valueOf(3))
            .thursday(BigDecimal.valueOf(4))
            .friday(BigDecimal.valueOf(5))
            .saturday(BigDecimal.valueOf(6))
            .sunday(BigDecimal.valueOf(7))
            .build();

        final WorkingTime actual = sut.updateWorkingTime(workingTime);

        assertThat(actual).isNotSameAs(workingTime);
        assertThat(actual.getUserId()).isEqualTo(new UserLocalId(42L));
        assertThat(actual.getMonday()).hasValue(WorkDay.monday(Duration.ofHours(1)));
        assertThat(actual.getTuesday()).hasValue(WorkDay.tuesday(Duration.ofHours(2)));
        assertThat(actual.getWednesday()).hasValue(WorkDay.wednesday(Duration.ofHours(3)));
        assertThat(actual.getThursday()).hasValue(WorkDay.thursday(Duration.ofHours(4)));
        assertThat(actual.getFriday()).hasValue(WorkDay.friday(Duration.ofHours(5)));
        assertThat(actual.getSaturday()).hasValue(WorkDay.saturday(Duration.ofHours(6)));
        assertThat(actual.getSunday()).hasValue(WorkDay.sunday(Duration.ofHours(7)));

        final ArgumentCaptor<WorkingTimeEntity> captor = ArgumentCaptor.forClass(WorkingTimeEntity.class);
        verify(workingTimeRepository).save(captor.capture());

        final WorkingTimeEntity actualEntity = captor.getValue();
        assertThat(actualEntity.getUserId()).isEqualTo(42L);
        assertThat(actualEntity.getMonday()).isEqualTo("PT1H");
        assertThat(actualEntity.getTuesday()).isEqualTo("PT2H");
        assertThat(actualEntity.getWednesday()).isEqualTo("PT3H");
        assertThat(actualEntity.getThursday()).isEqualTo("PT4H");
        assertThat(actualEntity.getFriday()).isEqualTo("PT5H");
        assertThat(actualEntity.getSaturday()).isEqualTo("PT6H");
        assertThat(actualEntity.getSunday()).isEqualTo("PT7H");
    }
}