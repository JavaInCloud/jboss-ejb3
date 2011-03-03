/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.ejb3.pool;

import org.jboss.ejb3.pool.legacy.BeanContext;
import org.jboss.ejb3.pool.legacy.Container;
import org.jboss.ejb3.pool.legacy.Injector;
import org.jboss.logging.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The base of all pool implementations.
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision$
 */
public abstract class AbstractPool implements Pool
{
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(AbstractPool.class);

   protected Injector[] injectors;
   protected Container container;
   private AtomicInteger createCount = new AtomicInteger(0);
   private AtomicInteger removeCount = new AtomicInteger(0);

   public AbstractPool()
   {

   }

   public int getCreateCount()
   {
      return createCount.get();
   }

   public int getRemoveCount()
   {
      return removeCount.get();
   }

   public void initialize(Container container, int maxSize, long timeout)
   {
      assert container != null : "container is null";

      this.container = container;
   }

   public abstract void setMaxSize(int maxSize);

   @Deprecated
   protected BeanContext<?> create()
   {
      return create(null, null);
   }

   protected BeanContext<?> create(Class[] initTypes, Object[] initValues)
   {
      BeanContext ctx;
      ctx = createBeanContext();

      container.invokePostConstruct(ctx, initValues);

      // the init method only applies to stateful session beans

      createCount.incrementAndGet();

      return ctx;
   }

   private BeanContext createBeanContext()
   {
      return container.createBeanContext();
   }

   public void remove(BeanContext ctx)
   {
      this.doRemove(ctx);
   }

   public void discard(BeanContext<?> ctx)
   {
      this.doRemove(ctx);
   }

   public void setInjectors(Injector[] injectors)
   {
      this.injectors = injectors;
   }

   /**
    * Remove the bean context and invoke any callbacks
    * and track the remove count
    *
    * @param ctx
    */
   protected void doRemove(BeanContext<?> ctx)
   {
      try
      {
         container.invokePreDestroy(ctx);
      }
      finally
      {
         ctx.remove();
         removeCount.incrementAndGet();
      }
   }
}
