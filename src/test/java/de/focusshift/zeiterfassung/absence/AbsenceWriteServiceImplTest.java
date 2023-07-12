package de.focusshift.zeiterfassung.absence;

import de.focusshift.zeiterfassung.tenancy.tenant.TenantId;
import de.focusshift.zeiterfassung.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static de.focusshift.zeiterfassung.absence.AbsenceType.SPECIALLEAVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbsenceWriteServiceImplTest {

    private AbsenceWriteServiceImpl sut;

    @Mock
    private AbsenceRepository repository;

    @BeforeEach
    void setUp() {
        sut = new AbsenceWriteServiceImpl(repository);
    }

    @Test
    void ensureAddAbsence() {

        final Instant startDate = Instant.now();
        final Instant endDate = Instant.now();

        when(repository.findAllByTenantIdAndUserIdAndStartDateAndEndDateAndDayLengthAndType(
            "tenant-id",
            "user-id",
            startDate,
            endDate,
            DayLength.FULL,
            SPECIALLEAVE
        )).thenReturn(List.of());

        final AbsenceWrite absence = new AbsenceWrite(
            new TenantId("tenant-id"),
            new UserId("user-id"),
            startDate,
            endDate,
            DayLength.FULL,
            SPECIALLEAVE,
            AbsenceColor.BLUE
        );

        sut.addAbsence(absence);

        final ArgumentCaptor<AbsenceWriteEntity> captor = ArgumentCaptor.forClass(AbsenceWriteEntity.class);
        verify(repository).save(captor.capture());

        final AbsenceWriteEntity actualPersistedEntity = captor.getValue();
        assertThat(actualPersistedEntity.getTenantId()).isEqualTo("tenant-id");
        assertThat(actualPersistedEntity.getId()).isNull();
        assertThat(actualPersistedEntity.getUserId()).isEqualTo("user-id");
        assertThat(actualPersistedEntity.getStartDate()).isEqualTo(startDate);
        assertThat(actualPersistedEntity.getEndDate()).isEqualTo(endDate);
        assertThat(actualPersistedEntity.getDayLength()).isEqualTo(DayLength.FULL);
        assertThat(actualPersistedEntity.getType()).isEqualTo(SPECIALLEAVE);
        assertThat(actualPersistedEntity.getColor()).isEqualTo(AbsenceColor.BLUE);
    }

    @Test
    void ensureAddAbsenceDoesNothingWhenAbsencePotentiallyExistsAlready() {

        final Instant startDate = Instant.now();
        final Instant endDate = Instant.now();

        final AbsenceWriteEntity potentiallyExistingEntity = new AbsenceWriteEntity();

        when(repository.findAllByTenantIdAndUserIdAndStartDateAndEndDateAndDayLengthAndType(
            "tenant-id",
            "user-id",
            startDate,
            endDate,
            DayLength.FULL,
            SPECIALLEAVE
        )).thenReturn(List.of(potentiallyExistingEntity));

        final AbsenceWrite absence = new AbsenceWrite(
            new TenantId("tenant-id"),
            new UserId("user-id"),
            startDate,
            endDate,
            DayLength.FULL,
            SPECIALLEAVE,
            AbsenceColor.BLUE
        );

        sut.addAbsence(absence);

        verifyNoMoreInteractions(repository);
    }
}