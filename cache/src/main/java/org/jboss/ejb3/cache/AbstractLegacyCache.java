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
package org.jboss.ejb3.cache;

import org.jboss.ejb3.cache.legacy.StatefulBeanContext;

import javax.ejb.NoSuchEJBException;
import java.io.Serializable;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
@Deprecated
public abstract class AbstractLegacyCache implements StatefulCache
{
   private StatefulObjectFactory<StatefulBeanContext> factory;

   @Override
   public StatefulBeanContext create()
   {
      return create(null, null);
   }

   @Override
   public void discard(Serializable key)
   {
      remove(key);
   }

   @Override
   public StatefulBeanContext get(Serializable key) throws NoSuchEJBException
   {
      return get((Object) key);
   }

   protected abstract void remove(Object key);

   @Override
   public void remove(Serializable key)
   {
      remove((Object) key);
   }

   @Override
   public void setStatefulObjectFactory(StatefulObjectFactory<StatefulBeanContext> factory)
   {
      this.factory = factory;
   }
}
