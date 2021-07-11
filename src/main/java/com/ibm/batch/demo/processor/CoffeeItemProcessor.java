package com.ibm.batch.demo.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.ibm.batch.demo.vo.CoffeeVO;

public class CoffeeItemProcessor implements ItemProcessor<CoffeeVO, CoffeeVO> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeItemProcessor.class);

    @Override
    public CoffeeVO process(final CoffeeVO coffee) throws Exception {
 
        String brand = coffee.getBrand().toUpperCase();
        String origin = coffee.getOrigin().toUpperCase();
        String chracteristics = coffee.getCharacteristics().toUpperCase();

        CoffeeVO transformedCoffee = new CoffeeVO(brand, origin, chracteristics);
        LOGGER.info("Converting ( {} ) into ( {} )", coffee, transformedCoffee);

        return transformedCoffee;
    }

}

