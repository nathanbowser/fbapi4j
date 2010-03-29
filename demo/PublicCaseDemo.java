import java.io.File;

import com.fieldexpert.fbapi4j.Case;
import com.fieldexpert.fbapi4j.Configuration;
import com.fieldexpert.fbapi4j.Session;

public class PublicCaseDemo {

	public static void main(String[] args) {
		Configuration config = new Configuration();
		config.setProperty("endpoint", "https://fieldexpert.fogbugz.com/");
		config.setProperty("path", "default.asp");

		Session session = config.getSession();

		Case c = new Case("Proclaim", "Misc", "Public Submission Title", "This has been submmited by fbapi4j public submission mechanism.") //
				.attach(new File("test/fbapi4j.xml"));
		session.create(c);
	}

}
