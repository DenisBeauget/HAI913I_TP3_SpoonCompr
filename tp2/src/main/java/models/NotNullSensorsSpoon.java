package models;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;

public class NotNullSensorsSpoon extends AbstractProcessor<CtParameter<?>> {
	
	
	@Override
	public boolean isToBeProcessed(CtParameter<?> element) {
		return !element.getType().isPrimitive();// only for objects
	}
	
	
	
	@Override
	public void process(CtParameter<?> element) {
		// TODO Auto-generated method stub
		// we declare a new snippet of code to be inserted.
				CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();

				// this snippet contains an if check.
				final String value = String.format("if (%s == null) "
						+ "throw new IllegalArgumentException(\"[Spoon inserted check] null passed as parameter\");",
						element.getSimpleName());
				snippet.setValue(value);

				// we insert the snippet at the beginning of the method body.
				if (element.getParent(CtExecutable.class).getBody() != null) {
					element.getParent(CtExecutable.class).getBody().insertBegin(snippet);
				}
			}
	}

