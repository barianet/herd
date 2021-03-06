/*
* Copyright 2015 herd contributors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.finra.herd.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import org.finra.herd.dao.JmsMessageDao;
import org.finra.herd.model.jpa.JmsMessageEntity;
import org.finra.herd.model.jpa.JmsMessageEntity_;

@Repository
public class JmsMessageDaoImpl extends AbstractHerdDao implements JmsMessageDao
{
    @Override
    public JmsMessageEntity getOldestJmsMessage()
    {
        // Create the criteria builder and the criteria.
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<JmsMessageEntity> criteria = builder.createQuery(JmsMessageEntity.class);

        // The criteria root is the jms message.
        Root<JmsMessageEntity> jmsMessageEntity = criteria.from(JmsMessageEntity.class);

        // Add the select clause.
        criteria.select(jmsMessageEntity);

        // Add the order by clause, since we want to return only the oldest JMS message (a message with the smallest sequence generated id).
        criteria.orderBy(builder.asc(jmsMessageEntity.get(JmsMessageEntity_.id)));

        // Execute the query and ask it to return only the first row.
        List<JmsMessageEntity> resultList = entityManager.createQuery(criteria).setMaxResults(1).getResultList();

        // Return the result.
        return resultList.size() > 0 ? resultList.get(0) : null;
    }
}
