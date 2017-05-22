package com.cubeia.games.poker.admin.wicket.util;

import static org.quartz.CronExpression.validateExpression;

import java.text.ParseException;

import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class CronExpressionValidator extends Behavior implements IValidator<String> {
    private static final Logger log = LoggerFactory.getLogger(CronExpressionValidator.class);

	@Override
	public void validate(IValidatable<String> validatable) {
		String cron = validatable.getValue();
		
		try {
			validateExpression(cron);
		} catch (ParseException e) {
			log.debug("cron validation error: {}", e.getMessage());
			validatable.error(new ValidationError("Illegal cron expression: '" + cron + "'. " + e.getMessage()));
		}
	}

}
