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
package org.jboss.ejb3.effigy.int2;

import org.jboss.ejb3.effigy.AccessTimeoutEffigy;
import org.jboss.ejb3.effigy.ApplicationExceptionEffigy;
import org.jboss.ejb3.effigy.StatefulTimeoutEffigy;
import org.jboss.ejb3.effigy.common.JBossSessionBeanEffigy;
import org.jboss.metadata.ejb.jboss.JBossSessionBean31MetaData;
import org.jboss.metadata.ejb.spec.AccessTimeoutMetaData;
import org.jboss.metadata.ejb.spec.ApplicationExceptionMetaData;
import org.jboss.metadata.ejb.spec.ConcurrentMethodMetaData;
import org.jboss.metadata.ejb.spec.ConcurrentMethodsMetaData;
import org.jboss.metadata.ejb.spec.NamedMethodMetaData;
import org.jboss.metadata.ejb.spec.StatefulTimeoutMetaData;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class JBossSessionBean31Effigy extends JBossSessionBeanEffigy
{
   private Method afterBeginMethod;
   private Method afterCompletionMethod;
   private Method beforeCompletionMethod;
   private StatefulTimeoutEffigy statefulTimeout;
   private Map<Method, AccessTimeoutEffigy> accessTimeoutCache = new ConcurrentHashMap<Method, AccessTimeoutEffigy>();

   protected JBossSessionBean31Effigy(ClassLoader classLoader, JBossSessionBean31MetaData beanMetaData)
           throws ClassNotFoundException
   {
      super(classLoader, beanMetaData);

      this.afterBeginMethod = method(beanMetaData.getAfterBeginMethod());
      this.afterCompletionMethod = method(beanMetaData.getAfterCompletionMethod());
      this.beforeCompletionMethod = method(beanMetaData.getBeforeCompletionMethod());
      this.statefulTimeout = statefulTimeout(beanMetaData.getStatefulTimeout());
   }

   private static AccessTimeoutEffigy accessTimeout(AccessTimeoutMetaData accessTimeout)
   {
      if(accessTimeout == null)
         return null;
      return new JBossAccessTimeoutEffigy(accessTimeout.getTimeout(), accessTimeout.getUnit());
   }

   @Override
   protected ApplicationExceptionEffigy createApplicationExceptionEffigy(ClassLoader classLoader, ApplicationExceptionMetaData metaData)
           throws ClassNotFoundException
   {
      return new JBossApplicationException31Effigy(classLoader, metaData);
   }

   private AccessTimeoutEffigy findAccessTimeout(Method method)
   {
      AccessTimeoutMetaData accessTimeout = null;
      // best match
      JBossSessionBean31MetaData beanMetaData = getBeanMetaData();
      String params[] = params(method);
      ConcurrentMethodsMetaData concurrentMethods = beanMetaData.getConcurrentMethods();
      if(concurrentMethods != null)
      {
         ConcurrentMethodMetaData concurrentMethodMetaData = concurrentMethods.bestMatch(method.getName(), params);
         if(concurrentMethodMetaData != null)
         {
            // the concurrent-method might only contain a lock element, not an access-timeout
            accessTimeout = concurrentMethodMetaData.getAccessTimeout();
         }
      }
      if(accessTimeout == null)
         accessTimeout = beanMetaData.getAccessTimeout();
      return accessTimeout(accessTimeout);
   }

   @Override
   public AccessTimeoutEffigy getAccessTimeout(Method method)
   {
      AccessTimeoutEffigy timeout = accessTimeoutCache.get(method);
      if(timeout == null)
      {
         timeout = findAccessTimeout(method);
         if(timeout == null)
            timeout = JBossAccessTimeoutEffigy.NULL;
         accessTimeoutCache.put(method, timeout);
      }
      if(timeout == JBossAccessTimeoutEffigy.NULL)
         return null;
      return timeout;
   }

   @Override
   public Method getAfterBeginMethod()
   {
      return afterBeginMethod;
   }

   @Override
   public Method getAfterCompletionMethod()
   {
      return afterCompletionMethod;
   }

   @Override
   protected JBossSessionBean31MetaData getBeanMetaData()
   {
      return (JBossSessionBean31MetaData) super.getBeanMetaData();
   }

   @Override
   public Method getBeforeCompletionMethod()
   {
      return beforeCompletionMethod;
   }

   @Override
   public StatefulTimeoutEffigy getStatefulTimeout()
   {
      return statefulTimeout;
   }

   @Override
   protected Set<Class<?>> getAllViews(ClassLoader cl)
   {
      Set<Class<?>> views = super.getAllViews(cl);
      // check if it has a no-interface view
      JBossSessionBean31MetaData sessionBean = this.getBeanMetaData();
      if (sessionBean.isNoInterfaceBean())
      {
         if (views == null)
         {
            views = new HashSet<Class<?>>();
         }
         views.add(this.getEjbClass());
      }
      
      return views;
   }

   private Method method(NamedMethodMetaData namedMethod)
   {
      if(namedMethod == null)
         return null;

      Method method = ClassHelper.findMethod(getEjbClass(), namedMethod.getMethodName());
      method.setAccessible(true);
      return method;
   }

   private static String[] params(Method method)
   {
      Class<?> parameterTypes[] = method.getParameterTypes();
      String params[] = new String[parameterTypes.length];
      for(int i = 0; i < params.length; i++)
         params[i] = parameterTypes[i].getName();
      return params;
   }

   private static StatefulTimeoutEffigy statefulTimeout(StatefulTimeoutMetaData metaData)
   {
      if(metaData == null)
         return null;
      return new JBossStatefulTimeoutEffigy(metaData.getTimeout(), metaData.getUnit());
   }
}
