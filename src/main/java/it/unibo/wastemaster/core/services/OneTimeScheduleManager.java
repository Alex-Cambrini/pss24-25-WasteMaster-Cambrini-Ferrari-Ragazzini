package it.unibo.wastemaster.core.services;

import java.util.Calendar;
import java.util.Date;

import it.unibo.wastemaster.core.dao.OneTimeScheduleDAO;
import it.unibo.wastemaster.core.models.Collection;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;

public class OneTimeScheduleManager {

    private final OneTimeScheduleDAO oneTimeScheduleDAO;

    public OneTimeScheduleManager(OneTimeScheduleDAO oneTimeScheduleDAO) {
        this.oneTimeScheduleDAO = oneTimeScheduleDAO;
    }

    public void createOneTimeSchedule(Customer customer, Waste.WasteType wasteType, ScheduleStatus status,
            Date pickupDate) {
        OneTimeSchedule schedule = new OneTimeSchedule(customer, wasteType, status, pickupDate);
        oneTimeScheduleDAO.insert(schedule);
    }

    private boolean canModifyOrCancel(Date pickupDate, int cancelLimitDays) {
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pickupDate);
        calendar.add(Calendar.DAY_OF_MONTH, -cancelLimitDays);

        Date cancelLimitDate = calendar.getTime();
        return today.before(cancelLimitDate);
    }

    public boolean updateDateOneTimeSchedule(OneTimeSchedule schedule, Date newPickupDate) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (canModifyOrCancel(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setPickupDate(newPickupDate);
            oneTimeScheduleDAO.update(schedule);
            return true;
        }

        return false;
    }

    public boolean updateWasteTypeOneTimeSchedule(OneTimeSchedule schedule, Waste.WasteType wasteType) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (canModifyOrCancel(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setWasteType(wasteType);
            oneTimeScheduleDAO.update(schedule);
            return true;
        }

        return false;
    }

    public boolean cancelOneTimeSchedule(OneTimeSchedule schedule) {
        int scheduleId = schedule.getId();
        Collection collection = oneTimeScheduleDAO.findCollectionByScheduleId(scheduleId);

        if (collection == null)
            return false;

        int cancelLimitDays = collection.getCancelLimitDays();

        if (canModifyOrCancel(schedule.getPickupDate(), cancelLimitDays)) {
            schedule.setStatus(ScheduleStatus.CANCELLED);
            oneTimeScheduleDAO.update(schedule);
            return true;
        }

        return false;
    }

}
