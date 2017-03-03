/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package issues;

import issues.base.IssuesWithBase;
import issues.security.IssuesWithSecurity;
import issues.tracing.IssuesWithTracing;

/**
 *
 * @author vmc
 */
public class IssuesMain {
	public static void main(String[] argv) throws Exception {
		IssuesWithBase.main(null);
		IssuesWithTracing.main(null);
		IssuesWithSecurity.main(null);
	}
}
