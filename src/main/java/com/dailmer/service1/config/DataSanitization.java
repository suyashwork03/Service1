package com.dailmer.service1.config;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Map;
import java.util.Map.Entry;

import org.owasp.encoder.Encode;

import com.dailmer.service1.models.EmployeeOuterClass.Employee;

public class DataSanitization {
	boolean valid = false;

	// method to check xss.
	public static Map<String, Object> filterxss(Map<String, Object> request) {

		for (Entry<String, Object> entry : request.entrySet())
			if (entry.getValue() instanceof String) {
				// Normalizing string before encoding them.
				String normalized = Normalizer.normalize(entry.getValue().toString(), Form.NFKC);
				// browser will convert the encoded script back to original and display the
				// script as part of the web page but the browser will not run the script.
				entry.setValue(Encode.forHtml(normalized));
				entry.setValue(Encode.forCssString(normalized));
				entry.setValue(Encode.forCssUrl(normalized));
				entry.setValue(Encode.forJavaScript(normalized));
			}

		return request;

	}

	public static Employee filterxss(Employee request) {

		return Employee.newBuilder().setId(Encode.forHtml(request.getId())).setName(Encode.forHtml(request.getName()))
				.setDob(Encode.forHtml(request.getDob())).setSalary(Encode.forHtml(request.getSalary()))
				.setAge(Encode.forHtml(request.getAge())).setFileType(Encode.forHtml(request.getFileType())).build();

	}
	/*
	 * public static void preventXSS() {
	 * 
	 * String html = "<p><a href='http://example.com/'"
	 * +" onclick='checkData()'>Link</a></p>hello";
	 * 
	 * System.out.println("Initial HTML: " + html); String safeHtml =
	 * Jsoup.clean(html, Whitelist.none());
	 * 
	 * System.out.println("Cleaned HTML: " +safeHtml); }
	 */
}
