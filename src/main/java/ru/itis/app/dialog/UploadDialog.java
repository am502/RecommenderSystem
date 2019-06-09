package ru.itis.app.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

import java.util.Scanner;

public class UploadDialog extends Div {
	private Dialog mainDialog;
	private TextField url;
	private Button okButton;
	private Button cancelButton;
	private Scanner in;

	public UploadDialog() {
		mainDialog = new Dialog();
		mainDialog.setCloseOnEsc(false);
		mainDialog.setCloseOnOutsideClick(false);

		VerticalLayout vl = new VerticalLayout();

		url = new TextField();
		url.setSizeFull();
		url.setPlaceholder("Ссылка на библиотеку");

		MemoryBuffer buffer = new MemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.addSucceededListener(e -> in = new Scanner(buffer.getInputStream()));

		vl.add(url, upload);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeFull();

		okButton = new Button("Загрузить");
		okButton.setWidth("50%");

		cancelButton = new Button("Отмена");
		cancelButton.setWidth("50%");

		hl.add(cancelButton, okButton);

		mainDialog.add(vl, hl);

		add(mainDialog);
	}

	public void open() {
		mainDialog.open();
	}

	public void close() {
		mainDialog.close();
	}

	public TextField getUrl() {
		return url;
	}

	public Button getOkButton() {
		return okButton;
	}

	public Button getCancelButton() {
		return cancelButton;
	}

	public Scanner getIn() {
		return in;
	}
}
