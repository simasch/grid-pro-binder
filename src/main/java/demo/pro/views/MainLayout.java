package demo.pro.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import demo.pro.views.editor.EditorView;
import demo.pro.views.binder.GridProBinder;
import demo.pro.views.list.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * The main view is a top-level placeholder for other views.
 */
@PWA(name = "Pro Demo", shortName = "Pro Demo", enableInstallPrompt = false)
@Theme(themeFolder = "prodemo")
@PageTitle("Main")
public class MainLayout extends AppLayout {

    public static class MenuItemInfo {

        private String text;
        private String iconClass;
        private Class<? extends Component> view;

        public MenuItemInfo(String text, String iconClass, Class<? extends Component> view) {
            this.text = text;
            this.iconClass = iconClass;
            this.view = view;
        }

        public String getText() {
            return text;
        }

        public String getIconClass() {
            return iconClass;
        }

        public Class<? extends Component> getView() {
            return view;
        }

    }

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, createHeaderContent());
        addToDrawer(createDrawerContent());
    }

    private Component createHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.addClassName("text-secondary");
        toggle.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames("m-0", "text-l");

        Header header = new Header(toggle, viewTitle);
        header.addClassNames("bg-base", "border-b", "border-contrast-10", "box-border", "flex", "h-xl", "items-center",
                "w-full");
        return header;
    }

    private Component createDrawerContent() {
        H2 appName = new H2("Pro Demo");
        appName.addClassNames("flex", "items-center", "h-xl", "m-0", "px-m", "text-m");

        com.vaadin.flow.component.html.Section section = new com.vaadin.flow.component.html.Section(appName,
                createNavigation(), createFooter());
        section.addClassNames("flex", "flex-col", "items-stretch", "max-h-full", "min-h-full");
        return section;
    }

    private Nav createNavigation() {
        Nav nav = new Nav();
        nav.addClassNames("border-b", "border-contrast-10", "flex-grow", "overflow-auto");
        nav.getElement().setAttribute("aria-labelledby", "views");

        H3 views = new H3("Views");
        views.addClassNames("flex", "h-m", "items-center", "mx-m", "my-0", "text-s", "text-tertiary");
        views.setId("views");

        for (RouterLink link : createLinks()) {
            nav.add(link);
        }
        return nav;
    }

    private List<RouterLink> createLinks() {
        MenuItemInfo[] menuItems = new MenuItemInfo[]{ //
                new MenuItemInfo("List", "la la-th", ListView.class), //

                new MenuItemInfo("List with Binder", "la la-th", GridProBinder.class), //

                new MenuItemInfo("Editor", "la la-edit", EditorView.class), //

        };
        List<RouterLink> links = new ArrayList<>();
        for (MenuItemInfo menuItemInfo : menuItems) {
            links.add(createLink(menuItemInfo));

        }
        return links;
    }

    private static RouterLink createLink(MenuItemInfo menuItemInfo) {
        RouterLink link = new RouterLink();
        link.addClassNames("flex", "mx-s", "p-s", "relative", "text-secondary");
        link.setRoute(menuItemInfo.getView());

        Span icon = new Span();
        icon.addClassNames("me-s", "text-l");
        if (!menuItemInfo.getIconClass().isEmpty()) {
            icon.addClassNames(menuItemInfo.getIconClass());
        }

        Span text = new Span(menuItemInfo.getText());
        text.addClassNames("font-medium", "text-s");

        link.add(icon, text);
        return link;
    }

    private Footer createFooter() {
        Footer layout = new Footer();
        layout.addClassNames("flex", "items-center", "my-s", "px-m", "py-xs");

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
