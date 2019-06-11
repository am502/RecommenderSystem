package ru.itis.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itis.dto.CollaborativeRecommendationsDto;
import ru.itis.model.Article;
import ru.itis.service.MainService;

@Route("personal")
public class PersonalView extends VerticalLayout {
	@Autowired
	private MainService mainService;

	public PersonalView() {
		HorizontalLayout hl = new HorizontalLayout();

		TextField username = new TextField();
		username.setWidth("500px");
		username.setPlaceholder("Имя пользователя");

		ListBox<String> recommendations = new ListBox<>();
		recommendations.setReadOnly(true);
		recommendations.setVisible(false);

		Button findButton = new Button("Найти");
		findButton.addClickListener(e -> {
			CollaborativeRecommendationsDto collaborativeRecommendationsDto = mainService
					.getPersonalRecommendations(username.getValue());

			recommendations.setItems(collaborativeRecommendationsDto
					.getRecommendations().stream().map(Article::getTitle));
			recommendations.setVisible(true);
		});

		hl.add(username, findButton);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.add(recommendations);
		contentLayout.setWidth("600px");

		add(hl, contentLayout);

		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}
}
