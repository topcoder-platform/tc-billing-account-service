/*
 * Copyright (C) 2018 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.test.api;

import com.appirio.service.billingaccount.api.BillingAccount;

import org.junit.Assert;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


/**
 * Provides helper method for test classes.
 *
 * @author TCSCODER
 * @version 1.0
 */
public final class TestHelper {
    /**
     * Convert string representation in format of yyyy-MM-dd'T'HH:mm'Z' to Date object
     *
     * @param dateStr
     *            the date string
     * @return Date object
     */
    public static Date toDate(String dateTimeStr) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'").withZone(ZoneId.of(
            "GMT"));
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTimeStr, formatter);
        return Date.from(zonedDateTime.toInstant());
    }

    /**
     * Asserts that given actual object is equal to the expected one
     *
     * @param type
     *            the type of the object to assert
     * @param expected
     *            the expected
     * @param actual
     *            the actual
     * @throws IntrospectionException
     *             if any error occurred while performing introspection
     * @throws ReflectiveOperationException
     *             if any error occurred while reading property's value
     */
    public static void assertEquals(Class<?> type, Object expected, Object actual) throws IntrospectionException,
        ReflectiveOperationException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            Method method = descriptor.getReadMethod();
            if (method != null) {
                if (descriptor.getPropertyType() == List.class) {
                    List<?> expectedList = (List<?>) method.invoke(expected);
                    List<?> actualList = (List<?>) method.invoke(actual);
                    for (int i = 0; i < expectedList.size(); i++) {
                        assertEquals(expectedList.get(0).getClass(), expectedList.get(0), actualList.get(0));
                    }
                } else if (descriptor.getPropertyType() == BillingAccount.class) {
                    assertEquals(BillingAccount.class, method.invoke(expected), method.invoke(actual));
                } else {
                    Assert.assertEquals(descriptor.getName(), method.invoke(expected), method.invoke(actual));
                }
            }
        }
    }
}
