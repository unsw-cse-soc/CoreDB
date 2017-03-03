/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package coredb.test;

import issues.base.IssuesWithBase;
import issues.security.IssuesWithSecurity;
import issues.tracing.IssuesWithTracing;

/**
 *
 * @author vmc
 */
public class TestSampleCode {
	public static void main(String[] argv) throws Exception {
		TestSampleCodeWithBase.main(null);
		IssuesWithBase.main(null);
		TestSampleCodeWithTracing.main(null);
		IssuesWithTracing.main(null);
		TestSampleCodeWithSecurity.main(null);
		IssuesWithSecurity.main(null);
		//IssuerAlternativeNameExtension();

		System.out.println("FINISH ALL TESTCASES");
	}

}
