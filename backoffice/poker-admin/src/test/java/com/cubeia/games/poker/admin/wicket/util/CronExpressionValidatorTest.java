package com.cubeia.games.poker.admin.wicket.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.junit.Test;
import org.mockito.Mockito;

public class CronExpressionValidatorTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testValidateError() {
		CronExpressionValidator validator = new CronExpressionValidator();
		
		IValidatable<String> validatable = mock(IValidatable.class);
		when(validatable.getValue()).thenReturn("invalido");
		
		validator.validate(validatable);
		
		verify(validatable).error(Mockito.any(ValidationError.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValidateNoError() {
		CronExpressionValidator validator = new CronExpressionValidator();
		
		IValidatable<String> validatable = mock(IValidatable.class);
		when(validatable.getValue()).thenReturn("0 30 14 * * ?");
		
		validator.validate(validatable);
		
		verify(validatable, Mockito.never()).error(Mockito.any(ValidationError.class));
	}
	
}
