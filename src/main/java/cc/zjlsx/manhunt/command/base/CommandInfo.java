package cc.zjlsx.manhunt.command.base;

import cc.zjlsx.manhunt.enums.Permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target (ElementType.TYPE)
@Retention (RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();

    Permissions permission() default Permissions.None;

    boolean requiresPlayer() default false;
}
