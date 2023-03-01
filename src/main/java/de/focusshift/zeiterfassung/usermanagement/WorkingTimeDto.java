package de.focusshift.zeiterfassung.usermanagement;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class WorkingTimeDto {

    private Long userId;
    private List<String> workday = new ArrayList<>();
    private Double workingTime;
    private Double workingTimeMonday;
    private Double workingTimeTuesday;
    private Double workingTimeWednesday;
    private Double workingTimeThursday;
    private Double workingTimeFriday;
    private Double workingTimeSaturday;
    private Double workingTimeSunday;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<String> getWorkday() {
        return workday;
    }

    public void setWorkday(List<String> workday) {
        this.workday = workday;
    }

    public Double getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Double workingTime) {
        this.workingTime = workingTime;
    }

    public Double getWorkingTimeMonday() {
        return workingTimeMonday;
    }

    public void setWorkingTimeMonday(Double workingTimeMonday) {
        this.workingTimeMonday = workingTimeMonday;
    }

    public Double getWorkingTimeTuesday() {
        return workingTimeTuesday;
    }

    public void setWorkingTimeTuesday(Double workingTimeTuesday) {
        this.workingTimeTuesday = workingTimeTuesday;
    }

    public Double getWorkingTimeWednesday() {
        return workingTimeWednesday;
    }

    public void setWorkingTimeWednesday(Double workingTimeWednesday) {
        this.workingTimeWednesday = workingTimeWednesday;
    }

    public Double getWorkingTimeThursday() {
        return workingTimeThursday;
    }

    public void setWorkingTimeThursday(Double workingTimeThursday) {
        this.workingTimeThursday = workingTimeThursday;
    }

    public Double getWorkingTimeFriday() {
        return workingTimeFriday;
    }

    public void setWorkingTimeFriday(Double workingTimeFriday) {
        this.workingTimeFriday = workingTimeFriday;
    }

    public Double getWorkingTimeSaturday() {
        return workingTimeSaturday;
    }

    public void setWorkingTimeSaturday(Double workingTimeSaturday) {
        this.workingTimeSaturday = workingTimeSaturday;
    }

    public Double getWorkingTimeSunday() {
        return workingTimeSunday;
    }

    public void setWorkingTimeSunday(Double workingTimeSunday) {
        this.workingTimeSunday = workingTimeSunday;
    }


    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return always {@code false}
     */
    public boolean isEmpty() {
        return false;
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when monday is selected, {@code false} otherwise
     */
    public boolean isWorkDayMonday() {
        return getWorkday().contains("monday");
    }

        /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when tuesday is selected, {@code false} otherwise
     */
    public boolean isWorkDayTuesday() {
        return getWorkday().contains("tuesday");
    }

        /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when wednesday is selected, {@code false} otherwise
     */
    public boolean isWorkDayWednesday() {
        return getWorkday().contains("wednesday");
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when thursday is selected, {@code false} otherwise
     */
    public boolean isWorkDayThursday() {
        return getWorkday().contains("thursday");
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when friday is selected, {@code false} otherwise
     */
    public boolean isWorkDayFriday() {
        return getWorkday().contains("friday");
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when saturday is selected, {@code false} otherwise
     */
    public boolean isWorkDaySaturday() {
        return getWorkday().contains("saturday");
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return {@code true} when sunday is selected, {@code false} otherwise
     */
    public boolean isWorkDaySunday() {
        return getWorkday().contains("sunday");
    }

    /**
     * marker to ease validation feedback for the user. field error is set in {@linkplain WorkingTimeDtoValidator}.
     * @return always {@code false}
     */
    public boolean isWorkingTimeClash() {
        return false;
    }

    @Override
    public String toString() {
        return "WorkingTimeDto{" +
            "userId=" + userId +
            ", workday=" + workday +
            ", workingtime=" + workingTime +
            ", workingtimeMonday=" + workingTimeMonday +
            ", workingtimeTuesday=" + workingTimeTuesday +
            ", workingtimeWednesday=" + workingTimeWednesday +
            ", workingtimeThursday=" + workingTimeThursday +
            ", workingtimeFriday=" + workingTimeFriday +
            ", workingtimeSaturday=" + workingTimeSaturday +
            ", workingtimeSunday=" + workingTimeSunday +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkingTimeDto that = (WorkingTimeDto) o;
        return userId.equals(that.userId)
            && workday.equals(that.workday)
            && Objects.equals(workingTime, that.workingTime)
            && Objects.equals(workingTimeMonday, that.workingTimeMonday)
            && Objects.equals(workingTimeTuesday, that.workingTimeTuesday)
            && Objects.equals(workingTimeWednesday, that.workingTimeWednesday)
            && Objects.equals(workingTimeThursday, that.workingTimeThursday)
            && Objects.equals(workingTimeFriday, that.workingTimeFriday)
            && Objects.equals(workingTimeSaturday, that.workingTimeSaturday)
            && Objects.equals(workingTimeSunday, that.workingTimeSunday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, workday, workingTime, workingTimeMonday, workingTimeTuesday,
            workingTimeWednesday, workingTimeThursday, workingTimeFriday, workingTimeSaturday, workingTimeSunday);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private List<DayOfWeek> workday = new ArrayList<>();
        private Double workingTime;
        private Double workingTimeMonday;
        private Double workingTimeTuesday;
        private Double workingTimeWednesday;
        private Double workingTimeThursday;
        private Double workingTimeFriday;
        private Double workingTimeSaturday;
        private Double workingTimeSunday;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder workday(List<DayOfWeek> workday) {
            this.workday = workday;
            return this;
        }

        public Builder workingTime(Double workingTime) {
            this.workingTime = workingTime;
            return this;
        }

        public Builder workingTimeMonday(Double workingTimeMonday) {
            this.workingTimeMonday = workingTimeMonday;
            return this;
        }

        public Builder workingTimeTuesday(Double workingTimeTuesday) {
            this.workingTimeTuesday = workingTimeTuesday;
            return this;
        }

        public Builder workingTimeWednesday(Double workingTimeWednesday) {
            this.workingTimeWednesday = workingTimeWednesday;
            return this;
        }

        public Builder workingTimeThursday(Double workingTimeThursday) {
            this.workingTimeThursday = workingTimeThursday;
            return this;
        }

        public Builder workingTimeFriday(Double workingTimeFriday) {
            this.workingTimeFriday = workingTimeFriday;
            return this;
        }

        public Builder workingTimeSaturday(Double workingTimeSaturday) {
            this.workingTimeSaturday = workingTimeSaturday;
            return this;
        }

        public Builder workingTimeSunday(Double workingTimeSunday) {
            this.workingTimeSunday = workingTimeSunday;
            return this;
        }

        public WorkingTimeDto build() {
            final WorkingTimeDto workingTimeDto = new WorkingTimeDto();
            workingTimeDto.setUserId(userId);
            workingTimeDto.setWorkday(workday.stream().map(DayOfWeek::name).map(String::toLowerCase).toList());
            workingTimeDto.setWorkingTime(workingTime);
            workingTimeDto.setWorkingTimeMonday(workingTimeMonday);
            workingTimeDto.setWorkingTimeTuesday(workingTimeTuesday);
            workingTimeDto.setWorkingTimeWednesday(workingTimeWednesday);
            workingTimeDto.setWorkingTimeThursday(workingTimeThursday);
            workingTimeDto.setWorkingTimeFriday(workingTimeFriday);
            workingTimeDto.setWorkingTimeSaturday(workingTimeSaturday);
            workingTimeDto.setWorkingTimeSunday(workingTimeSunday);
            return workingTimeDto;
        }
    }
}