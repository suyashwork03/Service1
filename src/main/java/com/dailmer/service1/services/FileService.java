package com.dailmer.service1.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dailmer.service1.models.EmployeeOuterClass.Employee;
import com.dailmer.service2.models.DataModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Service
public class FileService {

	@Autowired
	OkHttpClient client;

	@Value("${encrypt.key}")
	String myKey;

	@Value("${service.url}")
	String service2URL;

	private static SecretKeySpec secretKey;
	private static byte[] key;

	/**
	 * Method to get Data from Service2
	 * 
	 * @param request with fileName
	 * @return Data of the file
	 * 
	 */
	public DataModel getData(Map<String, Object> request) throws InvalidProtocolBufferException, IOException {
		okhttp3.RequestBody requestBody = null;
		try {
			requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json"),
					new ObjectMapper().writeValueAsString(request));

		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Request httpRequest = new Request.Builder().url(service2URL + "getFileData").put(requestBody).build();
		Response response = null;
		try {
			response = client.newCall(httpRequest).execute();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setKey();
		Employee emp = Employee.parseFrom(decrypt(response.body().bytes()));
		DataModel model = new DataModel(emp.getName(), emp.getDob(), emp.getSalary(), emp.getAge());
		return model;

	}

	/**
	 * / Method to get Key for encryption
	 */
	public void setKey() {

		MessageDigest sha = null;
		try {
			try {
				key = myKey.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to encrypt byte array using Generated Key
	 * 
	 * @param byteArrayToEncrypt
	 * @return encrypted data
	 */
	public String encrypt(byte[] byteArrayToEncrypt) {

		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(byteArrayToEncrypt));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * Method to encrypt byte array using Generated Key
	 * 
	 * @param byteArrayToEncrypt
	 * @return encrypted data
	 */
	public byte[] decrypt(byte[] strToDecrypt) {
		try {

			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
		} catch (Exception e) {
			System.out.println("Error while decrypting: " + e.toString());
		}
		return null;
	}

	/**
	 * Method to encrypt Employee data and Convert to Google Buffer Protocol String
	 * 
	 * @param employee
	 * @return Google Buffer Protocol String Equivalent of Employee
	 */
	public String convertToString(Employee employee) {
		setKey();
		String data = encrypt(employee.toByteArray());
		return data;
	}

}
