package demo.pro.views.binder;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import demo.pro.views.MainLayout;

import java.util.Collection;
import java.util.List;

@Route(layout = MainLayout.class)
public class GridProBinder extends Div {

    private final GridPro<Person> grid = new GridPro<>();

    private Collection<Person> createExamplePersons() {
        Person simon = new Person();
        simon.setFirstName("Simon");
        simon.setLastName("Martinelli");
        simon.setEmail("simon@martinelli.ch");

        Person kaspar = new Person();
        kaspar.setFirstName("Kaspar");
        kaspar.setLastName("Walter");
        kaspar.setEmail("kaspar.walter@dynasoft.ch");
        kaspar.setAktiv("N");

        return List.of(simon, kaspar);
    }

    public GridProBinder() {
        // Add Binder
        Binder<Person> binder = new Binder<>();

        // Setup a grid with random data
        grid.setItems(createExamplePersons());
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        // Double click selects field text which is annoying
        // grid.setEditOnClick(true);

        // Use custom editor
        TextField firstName = createTextField();

        // Bind with Validators
        binder.forField(firstName).asRequired("First name is required")
                .withValidator(new StringLengthValidator(
                        "First name(s) can be 1 - 80 characters long", 1, 80))
                // Use notification for showing validation error
                .withValidationStatusHandler(
                        handler -> showValidationError(handler, binder))
                .bind(Person::getFirstName, Person::setFirstName);

        addEditColumnWithBinder(binder, Person::getFirstName, firstName,
                "First name(s)");

        TextField lastName = createTextField();

        binder.forField(lastName)
                .withValidator(new StringLengthValidator(
                        "Last name can be 1 - 20 characters long", 1, 20))
                .withValidator(Validator.from(value -> !value.contains(" "),
                        "Last name cannot contain spaces"))
                // Use notification for showing validation error
                .withValidationStatusHandler(
                        handler -> showValidationError(handler, binder))
                .bind(Person::getLastName, Person::setLastName);

        addEditColumnWithBinder(binder, Person::getLastName, lastName,
                "Last name");

        TextField emailField = createTextField();

        binder.forField(emailField).asRequired("E-mail is required")
                .withValidator(new EmailValidator("Input valid e-mail"))
                .withValidationStatusHandler(
                        handler -> showValidationError(handler, binder))
                .bind(Person::getEmail, Person::setEmail);

        addEditColumnWithBinder(binder, Person::getEmail, emailField, "E-mail");

        Checkbox checkbox = new Checkbox();
        binder.forField(checkbox)
                .withConverter(new Converter<Boolean, String>() {
                    @Override
                    public Result<String> convertToModel(Boolean value, ValueContext context) {
                        return Result.ok(value ? "J" : "N");
                    }

                    @Override
                    public Boolean convertToPresentation(String value, ValueContext context) {
                        return value.equals("J");
                    }
                })
                .bind(Person::getAktiv, Person::setAktiv);

        addEditColumnWithBinder(binder, Person::getAktiv, checkbox, "Aktiv");

        grid.addCellEditStartedListener(event -> {
            // We prefer using setBean as GridPro is by nature un-buffered
            binder.setBean(event.getItem());
        });
        grid.addItemPropertyChangedListener(event -> {
            // This is required to avoid accidental copy pasting of value
            binder.setBean(null);
        });
        add(grid);
    }

    // Convenience method for adding column with given text field, etc.
    private void addEditColumnWithBinder(Binder<Person> binder,
                                         ValueProvider<Person, ?> valueProvider, AbstractField<?, ?> abstractField,
                                         String header) {
        grid.addEditColumn(valueProvider)
                .custom(abstractField, (item, newValue) -> {
                    // Intentionally NOP as value is committed by Binder as we use setBean
                }).setHeader(header);
    }

    // Convenience method for creating a new textfield
    private TextField createTextField() {
        TextField textField = new MyTextField();
        textField.setWidth("100%");
        textField.setAutoselect(false);
        textField.addThemeName("grid-pro-editor");
        return textField;
    }

    // Convenience method for showing the validation error as Notification
    private void showValidationError(BindingValidationStatus<?> handler, Binder<Person> binder) {
        if (binder.getBean() != null && handler.isError()) {
            Notification
                    .show("Validation: " + handler.getMessage().get(), 5000,
                            Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    // Convenience solution for missing required indicator
    public static class MyTextField extends TextField {

        @Override
        public void setRequiredIndicatorVisible(boolean visible) {
            super.setRequiredIndicatorVisible(visible);
            if (visible) {
                setPlaceholder("required");
            } else {
                setPlaceholder(null);
            }
        }
    }

    public static class Person {
        private String firstName, lastName;
        private String email = "";
        private String aktiv = "J";

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAktiv() {
            return aktiv;
        }

        public void setAktiv(String aktiv) {
            this.aktiv = aktiv;
        }
    }
}
