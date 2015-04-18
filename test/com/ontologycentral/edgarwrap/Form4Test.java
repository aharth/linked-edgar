package com.ontologycentral.edgarwrap;

import junit.framework.TestCase;

import com.ontologycentral.edgarwrap.Form4;

public class Form4Test extends TestCase {
	public void testForm4() throws Exception {
		Form4 form = new Form4(null);
		
		form.tranform("1000045", "0001000045-10-000020");
	}
}
