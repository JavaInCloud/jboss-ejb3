/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.ejb3.timerservice.mk2.task;

import org.jboss.ejb3.timerservice.mk2.CalendarTimer;
import org.jboss.ejb3.timerservice.spi.TimedObjectInvoker;
import org.jboss.logging.Logger;
import org.jboss.ejb3.timerservice.spi.MultiTimeoutMethodTimedObjectInvoker;

/**
 * CalendarTimerTask
 *
 * @author Jaikiran Pai
 * @version $Revision: $
 */
public class CalendarTimerTask extends TimerTask<CalendarTimer>
{

   /**
    * Logger
    */
   private static Logger logger = Logger.getLogger(CalendarTimerTask.class);

   /**
    * 
    * @param calendarTimer
    */
   public CalendarTimerTask(CalendarTimer calendarTimer)
   {
      super(calendarTimer);
   }

   @Override
   protected void handleTimeout() throws Exception
   {
      CalendarTimer calendarTimer = this.getTimer();

      calendarTimer.scheduleTimeout();
      // finally invoke the timeout method through the invoker
      if (calendarTimer.isAutoTimer())
      {
         TimedObjectInvoker invoker = this.timerService.getInvoker();
         if (invoker instanceof MultiTimeoutMethodTimedObjectInvoker == false)
         {
            String msg = "Cannot invoke timeout method because timer: " + calendarTimer
                  + " is an auto timer, but invoker is not of type" + MultiTimeoutMethodTimedObjectInvoker.class;
            logger.error(msg);
            throw new RuntimeException(msg);
         }
         // call the timeout method
         ((MultiTimeoutMethodTimedObjectInvoker) invoker).callTimeout(calendarTimer, calendarTimer.getTimeoutMethod());
      }
      else
      {
         this.timerService.getInvoker().callTimeout(calendarTimer);
      }
   }

}
