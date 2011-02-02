/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
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
package org.jboss.ejb3.interceptors.effigy;

import org.jboss.ejb3.effigy.InterceptorEffigy;
import org.jboss.ejb3.effigy.SessionBeanEffigy;
import org.jboss.ejb3.interceptors.container.AbstractContainer;
import org.jboss.ejb3.interceptors.container.AbstractContainerTestCase;
import org.jboss.ejb3.interceptors.container.AbstractContainerTestCaseHelper;
import org.jboss.ejb3.interceptors.container.SimpleBean;
import org.jboss.ejb3.interceptors.container.SimpleInterceptor;
import org.jboss.interceptor.proxy.DefaultInvocationContextFactory;
import org.jboss.interceptor.proxy.DirectClassInterceptorInstantiator;
import org.jboss.interceptor.spi.context.InvocationContextFactory;
import org.jboss.interceptor.spi.instance.InterceptorInstantiator;
import org.junit.Test;

import static org.jboss.ejb3.effigy.dsl.InterceptorFactory.interceptor;
import static org.jboss.ejb3.effigy.dsl.SessionBeanFactory.session;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class TransformerTestCase extends AbstractContainerTestCaseHelper
{
   @Test
   public void test1() throws Exception
   {
      InterceptorEffigy interceptor = interceptor(SimpleInterceptor.class).
              aroundInvoke("aroundInvoke").
              postConstruct("postConstruct").
              effigy();
      SessionBeanEffigy bean = session(SimpleBean.class).
              interceptAllWith(interceptor).
              effigy();

      Transformer transformer = new Transformer(bean);

      InterceptorInstantiator<?,?> interceptorInstantiator = new DirectClassInterceptorInstantiator();

      AbstractContainer container = new AbstractContainer(transformer.getBeanClassInterceptorMetadata(), transformer.getInterceptionModel(), interceptorInstantiator);

      testContainer(container);
   }
}
