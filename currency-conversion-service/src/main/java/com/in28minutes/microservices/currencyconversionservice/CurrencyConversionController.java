package com.in28minutes.microservices.currencyconversionservice;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CurrencyConversionController {
	@Autowired
	private CurrencyExchangeProxy currencyExchangeProxy;

	@GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConvesion calculateCurrencyConversion(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		HashMap<String, String> uriVariables = new HashMap<String, String>();
		uriVariables.put("from", from);
		uriVariables.put("to", to);
		ResponseEntity<CurrencyConvesion> responseEntity = new RestTemplate().getForEntity(
				"http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyConvesion.class, uriVariables);
		CurrencyConvesion currencyConvesion = responseEntity.getBody();
		return new CurrencyConvesion(currencyConvesion.getId(), from, to, currencyConvesion.getConversionMultiple(),
				quantity, quantity.multiply(currencyConvesion.getConversionMultiple()),
				currencyConvesion.getEnvironment()+" rest template");
	}

	@GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConvesion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
			@PathVariable BigDecimal quantity) {
		CurrencyConvesion currencyConvesion = currencyExchangeProxy.retrieveExchangedValue(from, to);
		return new CurrencyConvesion(currencyConvesion.getId(), from, to, currencyConvesion.getConversionMultiple(),
				quantity, quantity.multiply(currencyConvesion.getConversionMultiple()),
				currencyConvesion.getEnvironment()+" feign");
	}
}
