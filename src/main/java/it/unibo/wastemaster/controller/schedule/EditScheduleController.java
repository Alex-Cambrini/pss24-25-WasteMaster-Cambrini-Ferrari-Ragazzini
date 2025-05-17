package it.unibo.wastemaster.controller.schedule;

import java.util.List;

import it.unibo.wastemaster.controller.utils.DialogUtils;
import it.unibo.wastemaster.core.context.AppContext;
import it.unibo.wastemaster.core.models.Customer;
import it.unibo.wastemaster.core.models.OneTimeSchedule;
import it.unibo.wastemaster.core.models.RecurringSchedule;
import it.unibo.wastemaster.core.models.Schedule.ScheduleStatus;
import it.unibo.wastemaster.core.models.Waste;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class EditScheduleController {

	private OneTimeSchedule oneTimeSchedule;
	private RecurringSchedule recurringSchedule;
	private ScheduleController scheduleController;

	@FXML
	private ComboBox<Waste> wasteComboBox;
	@FXML
	private ComboBox<ScheduleStatus> statusComboBox;
	@FXML
	private TextField customerField;
	@FXML
	private Label typeLabel;

	private List<Customer> allCustomers;
	private ContextMenu suggestionsMenu = new ContextMenu();

	public void setScheduleController(ScheduleController controller) {
		this.scheduleController = controller;
	}

	public void setScheduleToEdit(OneTimeSchedule schedule) {
		this.oneTimeSchedule = schedule;
		this.recurringSchedule = null;
		typeLabel.setText("OneTime");
		initCommonFields(schedule.getWaste(), schedule.getScheduleStatus(), schedule.getCustomer());
	}

	public void setScheduleToEdit(RecurringSchedule schedule) {
		this.recurringSchedule = schedule;
		this.oneTimeSchedule = null;
		typeLabel.setText("Recurring");
		initCommonFields(schedule.getWaste(), schedule.getScheduleStatus(), schedule.getCustomer());
	}

	private void initCommonFields(Waste waste, ScheduleStatus status, Customer customer) {
		allCustomers = AppContext.customerManager.getAllCustomers();
		wasteComboBox.setItems(FXCollections.observableArrayList(AppContext.wasteDAO.findAll()));
		statusComboBox.setItems(FXCollections.observableArrayList(ScheduleStatus.values()));

		wasteComboBox.setConverter(new StringConverter<Waste>() {
			@Override
			public String toString(Waste waste) {
				return waste != null ? waste.getWasteName() : "";
			}

			@Override
			public Waste fromString(String string) {
				return null;
			}
		});

		wasteComboBox.getSelectionModel().select(waste);
		statusComboBox.getSelectionModel().select(status);
		customerField.setText(customer.getName() + " " + customer.getSurname());

		customerField.textProperty().addListener((obs, oldText, newText) -> {
			if (newText == null || newText.isBlank()) {
				suggestionsMenu.hide();
				return;
			}
			List<Customer> matches = allCustomers.stream()
				.filter(c -> (c.getName() + " " + c.getSurname()).toLowerCase().contains(newText.toLowerCase()))
				.toList();

			if (matches.isEmpty()) {
				suggestionsMenu.hide();
				return;
			}

			suggestionsMenu.getItems().clear();
			for (Customer c : matches) {
				MenuItem item = new MenuItem(c.getName() + " " + c.getSurname());
				item.setOnAction(e -> {
					customerField.setText(c.getName() + " " + c.getSurname());
					suggestionsMenu.hide();
				});
				suggestionsMenu.getItems().add(item);
			}

			if (!suggestionsMenu.isShowing()) {
				suggestionsMenu.show(customerField, javafx.geometry.Side.BOTTOM, 0, 0);
			}
		});
	}

	@FXML
	private void handleUpdateSchedule() {
		try {
			Customer selectedCustomer = allCustomers.stream()
				.filter(c -> (c.getName() + " " + c.getSurname()).equalsIgnoreCase(customerField.getText().trim()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No matching customer found"));

			Waste selectedWaste = wasteComboBox.getValue();
			ScheduleStatus selectedStatus = statusComboBox.getValue();

			if (selectedWaste == null || selectedStatus == null)
				throw new IllegalArgumentException("Waste and Status are required");

			boolean changed = false;

			if (oneTimeSchedule != null) {
				changed = !oneTimeSchedule.getCustomer().equals(selectedCustomer)
					|| !oneTimeSchedule.getWaste().equals(selectedWaste)
					|| !oneTimeSchedule.getScheduleStatus().equals(selectedStatus);

				if (!changed) {
					DialogUtils.showError("No changes", "No fields were modified.");
					return;
				}

				oneTimeSchedule.setCustomer(selectedCustomer);
				oneTimeSchedule.setWaste(selectedWaste);
				oneTimeSchedule.setScheduleStatus(selectedStatus);
				AppContext.oneTimeScheduleDAO.update(oneTimeSchedule);

			} else if (recurringSchedule != null) {
				changed = !recurringSchedule.getCustomer().equals(selectedCustomer)
					|| !recurringSchedule.getWaste().equals(selectedWaste)
					|| !recurringSchedule.getScheduleStatus().equals(selectedStatus);

				if (!changed) {
					DialogUtils.showError("No changes", "No fields were modified.");
					return;
				}

				recurringSchedule.setCustomer(selectedCustomer);
				recurringSchedule.setWaste(selectedWaste);
				recurringSchedule.setScheduleStatus(selectedStatus);
				AppContext.recurringScheduleDAO.update(recurringSchedule);
			}

			DialogUtils.showSuccess("Schedule updated successfully.");
			scheduleController.returnToScheduleView();

		} catch (Exception e) {
			DialogUtils.showError("Update Error", e.getMessage());
		}
	}

	@FXML
	private void handleAbortScheduleEdit() {
		scheduleController.returnToScheduleView();
	}
}
