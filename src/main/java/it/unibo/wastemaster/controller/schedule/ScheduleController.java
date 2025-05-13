package it.unibo.wastemaster.controller.schedule;

import it.unibo.wastemaster.controller.main.MainLayoutController;
import it.unibo.wastemaster.controller.utils.DialogUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ScheduleController {

    @FXML
    private void handleAddSchedule() {
        try {
            MainLayoutController.getInstance().setPageTitle("Add Schedule");
            AddScheduleController controller = MainLayoutController.getInstance()
                    .loadCenterWithController("/layouts/schedule/AddScheduleView.fxml");
            controller.setScheduleController(this);
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Could not load Add Customer view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditSchedule(ActionEvent event) {
        // TODO
    }

    @FXML
    private void handleDeleteSchedule(ActionEvent event) {
        // TODO
    }

    @FXML
    private void showFilterMenu(javafx.scene.input.MouseEvent event) {
        // TODO
    }

    @FXML
    private void handleResetSearch(ActionEvent event) {
        // TODO
    }

    public void returnToScheduleView() {
        try {
            MainLayoutController.getInstance().restorePreviousTitle();
            MainLayoutController.getInstance().loadCenter("/layouts/schedule/ScheduleView.fxml");
        } catch (Exception e) {
            DialogUtils.showError("Navigation error", "Failed to load schedule view.");
        }
    }
}
