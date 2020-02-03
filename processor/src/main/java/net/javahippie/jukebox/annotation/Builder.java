package net.javahippie.jukebox.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotate any Java record with this annotation to create a builder class for it.
 *
 * As an example, the following record can be built like this:
 * <pre>{@code
 * {@literal @}Builder
 * public record Person(
 *          String firstName,
 *          String lastName,
 *          LocalDate birthDate,
 *          int heightInCentimeters){}
 *  }</pre>
 *
 *  <pre>{@code
 *  PersonBuilder.builder()
 *               .withFirstName("Frank")
 *               .withLastName("Meier")
 *               .withBirthDate(LocalDate.of(1990, 2, 2))
 *               .withHeightInCentimeters(170)
 *               .build()
 *  }</pre>
 *
 */
@Target(ElementType.TYPE)
public @interface Builder {

}
