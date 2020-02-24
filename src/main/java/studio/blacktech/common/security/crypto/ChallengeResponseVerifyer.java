package studio.blacktech.common.security.crypto;


import java.util.Calendar;
import java.util.TimeZone;

import studio.blacktech.common.security.RandomTool;


public class ChallengeResponseVerifyer {


	TimeZone timeZone = TimeZone.getTimeZone("UTC");
	Calendar calendar = Calendar.getInstance(timeZone);
	long timeBase = calendar.getTimeInMillis() / 60000;
	String challenge = RandomTool.randomStringBASE64(64);
	String answer = HashTool.sha256(timeBase + challenge);

	private String secret;

	public ChallengeResponseVerifyer(String secret) {
		this.secret = secret;
	}


	public void generateChallenge() {
		System.out.println(secret);
	}


}
