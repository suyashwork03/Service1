package com.dailmer.service1.controllers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dailmer.service1.config.DataSanitization;
import com.dailmer.service1.models.EmployeeOuterClass.Employee;
import com.dailmer.service1.services.FileService;
import com.google.protobuf.InvalidProtocolBufferException;

@RestController
public class FileController {
	@Autowired
	private FileService fileService;
	@Autowired
	private RSocketRequester rSocketRequester;

	/**
	 * Controller to Save Data as CSV/XML file , calls Service2 (RSocket Protocol)
	 * 
	 * @param employee
	 * @return FileName
	 */
	@RequestMapping(value = "/store", consumes = "application/json", produces = "text/plain", method = RequestMethod.PUT)
	public Publisher<String> store(@RequestBody Employee employee)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {

		return rSocketRequester.route("Save.File")
				.data(fileService.convertToString(DataSanitization.filterxss(employee))).retrieveMono(String.class);

	}

	/**
	 * Controller to update Data, calls Service2 (RSocket Protocol)
	 */
	@RequestMapping(value = "/update", consumes = "application/json", produces = "text/plain", method = RequestMethod.PUT)
	public Publisher<String> update(@RequestBody Employee employee)
			throws NoSuchAlgorithmException, NoSuchPaddingException, IOException {

		return rSocketRequester.route("Update.File")
				.data(fileService.convertToString(DataSanitization.filterxss(employee))).retrieveMono(String.class);
	}

	/**
	 * Controller to Get Data from CSV/XML file, calls Service2 (Http Protocol)
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/read", consumes = "application/json", produces = "application/json", method = RequestMethod.PUT)
	public ResponseEntity<Object> read(@RequestBody Map<String, Object> request)
			throws InvalidProtocolBufferException, IOException {

		return new ResponseEntity<>(fileService.getData(DataSanitization.filterxss(request)), HttpStatus.OK);
	}

}
