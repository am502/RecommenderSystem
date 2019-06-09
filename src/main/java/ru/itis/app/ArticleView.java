package ru.itis.app;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import ru.itis.dto.ContentRecommendationsDto;
import ru.itis.model.Article;
import ru.itis.service.MainService;

@Route("article")
public class ArticleView extends VerticalLayout {
	@Autowired
	private MainService mainService;

	public ArticleView() {
		HorizontalLayout hl = new HorizontalLayout();

		TextField title = new TextField();
		title.setWidth("500px");
		title.setPlaceholder("Название статьи");

		TextArea content = new TextArea();
		content.setSizeFull();
		content.setReadOnly(true);
		content.setVisible(false);

		ListBox<String> recommendations = new ListBox<>();
		recommendations.setReadOnly(true);
		recommendations.setVisible(false);

		Button findButton = new Button("Найти");
		findButton.addClickListener(e -> {
			ContentRecommendationsDto contentRecommendationsDto = mainService.getArticleByTitle(title.getValue());

			content.setValue(contentRecommendationsDto.getArticle().getContent());
			content.setVisible(true);

			recommendations.setItems(contentRecommendationsDto.getRecommendations().stream().map(Article::getTitle));
			recommendations.setVisible(true);
		});

		hl.add(title, findButton);

		HorizontalLayout contentLayout = new HorizontalLayout();
		contentLayout.add(content, recommendations);
		contentLayout.setSizeFull();

		add(hl, contentLayout);

		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}
}
