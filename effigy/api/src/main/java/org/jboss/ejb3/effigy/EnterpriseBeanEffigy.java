/*
 * JBoss, Home of Professional Open Source
 * Copyright (c) 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ejb3.effigy;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public interface EnterpriseBeanEffigy
{
   /**
    * Get all interceptors which are used in this EJB.
    */
   Iterable<InterceptorEffigy> getAllInterceptors();

   /**
    * Returns the applicable ApplicationExceptionEffigy for the given
    * exception. Note that this still not means that the exceptionClass
    * is an actual application exception in case the ApplicationExceptionEffigy
    * returned signifies a non-inherited super-class.
    *
    * @param exceptionClass the exception class
    * @return the application exception effigy or null if not an application exception
    */
   ApplicationExceptionEffigy getApplicationException(Class<?> exceptionClass);

   /**
    * An optional list of around-invoke methods declared on the bean class
    * and/or its super-classes.
    * 
    * The around-invoke type specifies a method on a
    * class to be called during the around invoke portion of an
    * ejb invocation.  Note that each class may have only one
    * around invoke method and that the method may not be
    * overloaded.
    *
    * Applies to message driven and session beans.
    * 
    * @return an optional list of around-invoke methods
    */
   Iterable<Method> getAroundInvokes();
   
   Class<?> getEjbClass();

   /**
    * Get the applicable interceptors for the given method.
    */
   Iterable<InterceptorEffigy> getInterceptors(Method method);
   
   /**
    * @return the ejb-name
    */
   String getName();

   /**
    * Specifies the methods to call on the bean whenever the post-construct
    * life-cycle callback occurs.
    *
    * @return an ordered list of bean methods
    */
   Iterable<Method> getPostConstructs();

   /**
    * Specifies the methods to call on the bean whenever the pre-destroy
    * life-cycle callback occurs.
    *
    * @return an ordered list of bean methods
    */
   Iterable<Method> getPreDestroys();

   /**
    *
    * @param method
    * @return
    */
   TransactionAttributeType getTransactionAttributeType(Method method);
}
