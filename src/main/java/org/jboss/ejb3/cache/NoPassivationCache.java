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
package org.jboss.ejb3.cache;

import java.util.HashMap;
import javax.ejb.EJBException;
import javax.ejb.NoSuchEJBException;
import org.jboss.ejb3.Container;
import org.jboss.ejb3.pool.Pool;
import org.jboss.ejb3.stateful.StatefulBeanContext;

/**
 * Comment
 *
 * @author <a href="mailto:bill@jboss.org">Bill Burke</a>
 * @version $Revision$
 */
public class NoPassivationCache implements StatefulCache
{   
   private Pool pool;
   private HashMap<Object, StatefulBeanContext> cacheMap;
   private int createCount = 0;
   private int removeCount = 0;

   public void initialize(Container container) throws Exception
   {
      this.pool = container.getPool();
      cacheMap = new HashMap<Object, StatefulBeanContext>();
   }

   public NoPassivationCache()
   {
   }

   public void start()
   {
   }

   public void stop()
   {
      synchronized (cacheMap)
      {
         cacheMap.clear();
      }
   }

   public StatefulBeanContext create()
   {
      return create(null, null);
   }
   
   public StatefulBeanContext create(Class[] initTypes, Object[] initValues)
   {
      StatefulBeanContext ctx = null;
      try
      {
         ctx = (StatefulBeanContext) pool.get(initTypes, initValues);
         ++createCount;
         synchronized (cacheMap)
         {
            cacheMap.put(ctx.getId(), ctx);
         }
      }
      catch (EJBException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return ctx;
   }

   public StatefulBeanContext get(Object key) throws EJBException
   {
      return get(key, true);
   }
   
   public StatefulBeanContext get(Object key, boolean markInUse) throws EJBException
   {
      StatefulBeanContext entry = null;
      synchronized (cacheMap)
      {
         entry = (StatefulBeanContext) cacheMap.get(key);
      }
      
      if (entry == null)
      {
         throw new NoSuchEJBException("Could not find Stateful bean: " + key);
      }      
      
      if (markInUse)
      {   
         if (entry.isRemoved())
         {
            throw new NoSuchEJBException("Could not find stateful bean: " + key +
                                         " (bean was marked as removed");
         }      
      
         entry.setInUse(true);
         entry.lastUsed = System.currentTimeMillis();
      }
      
      return entry;
   }

   public StatefulBeanContext peek(Object key) throws NoSuchEJBException
   {
      return get(key, false);
   }
   
   public void release(StatefulBeanContext ctx)
   {
      synchronized (ctx)
      {
         ctx.setInUse(false);
         ctx.lastUsed = System.currentTimeMillis();
      }
   }

   public void remove(Object key)
   {
      StatefulBeanContext ctx = null;
      synchronized (cacheMap)
      {
         ctx = (StatefulBeanContext) cacheMap.remove(key);
      }
      if (ctx != null)
      {
         pool.remove(ctx);
         ++removeCount;
      }
   }

   public int getCacheSize()
   {
	   return cacheMap.size();
   }
   
   public int getTotalSize()
   {
      return cacheMap.size();
   }
   
   public int getCreateCount()
   {
	   return createCount;
   }
   
   public int getPassivatedCount()
   {
	   return 0;
   }
   
   public int getRemoveCount()
   {
      return removeCount;
   }
   
   public int getAvailableCount()
   {
      return -1;
   }
   
   public int getMaxSize()
   {
      return -1;
   }
   
   public int getCurrentSize()
   {
      return cacheMap.size();
   }
}
