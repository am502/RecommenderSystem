package ru.itis.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itis.app.dialog.UploadDialog;
import ru.itis.service.MainService;

@Route("")
public class MainView extends VerticalLayout {
	@Autowired
	private MainService mainService;

	public MainView() {
		FormLayout fl = new FormLayout();
		fl.setSizeFull();

		Button articleButton = new Button("Найти статью");
		articleButton.addClickListener(e ->
				articleButton.getUI().ifPresent(ui -> ui.navigate("article")));

		Button recommendationsButton = new Button("Персональные реокмендации");
		recommendationsButton.addClickListener(e ->
				recommendationsButton.getUI().ifPresent(ui -> ui.navigate("personal")));

		UploadDialog uploadDialog = new UploadDialog();
		uploadDialog.getCancelButton().addClickListener(e -> uploadDialog.close());
		uploadDialog.getOkButton().addClickListener(e -> {
			uploadDialog.close();
			mainService.upload(uploadDialog.getUrl().getValue(), uploadDialog.getIn());
		});
		Button uploadButton = new Button("Загрузить данные");
		uploadButton.addClickListener(e -> uploadDialog.open());

		fl.add(articleButton, recommendationsButton, uploadButton);
		fl.setResponsiveSteps(
				new FormLayout.ResponsiveStep("0", 1),
				new FormLayout.ResponsiveStep("21em", 2),
				new FormLayout.ResponsiveStep("22em", 3)
		);

		add(fl, uploadDialog);
	}
}
