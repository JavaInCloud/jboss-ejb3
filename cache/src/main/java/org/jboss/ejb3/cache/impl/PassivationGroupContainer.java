/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.ejb3.cache.impl;

import org.jboss.ejb3.cache.PassivationManager;
import org.jboss.ejb3.cache.StatefulObjectFactory;
import org.jboss.ejb3.cache.grouped.PassivationGroup;
import org.jboss.logging.Logger;

/**
 * Comment
 *
 * @author <a href="mailto:carlo.dewolf@jboss.com">Carlo de Wolf</a>
 * @version $Revision$
 */
public class PassivationGroupContainer implements StatefulObjectFactory<PassivationGroup>, PassivationManager<PassivationGroup>
{
   private static final Logger log = Logger.getLogger(PassivationGroupContainer.class);

   @Override
   public PassivationGroup createInstance()
   {
      return new PassivationGroupImpl();
   }

   @Override
   public void destroyInstance(PassivationGroup obj)
   {
      // TODO: nothing?
   }

   @Override
   public void postActivate(PassivationGroup obj)
   {
      log.trace("post activate " + obj);
      ((PassivationGroupImpl) obj).postActivate();
   }

   @Override
   public void prePassivate(PassivationGroup obj)
   {
      log.trace("pre passivate " + obj);
      ((PassivationGroupImpl) obj).prePassivate();
   }

}
