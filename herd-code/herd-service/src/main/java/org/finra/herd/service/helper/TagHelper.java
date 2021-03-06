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
package org.finra.herd.service.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import org.finra.herd.model.api.xml.TagKey;

/**
 * A helper class for Tag related code
 */
@Component
public class TagHelper
{
    @Autowired
    private AlternateKeyHelper alternateKeyHelper;

    /**
     * Validates a tag key. This method also trims the key parameters.
     *
     * @param tagKey the tag key
     *
     * @throws IllegalArgumentException if any validation errors were found
     */
    public void validateTagKey(TagKey tagKey) throws IllegalArgumentException
    {
        // Validate.
        Assert.notNull(tagKey, "A tag key must be specified.");
        Assert.notNull(tagKey.getTagTypeCode(), "A tag type code must be specified.");
        Assert.notNull(tagKey.getTagCode(), "A tag code must be specified.");
        tagKey.setTagCode(alternateKeyHelper.validateStringParameter("tag code", tagKey.getTagCode()));
        tagKey.setTagTypeCode(alternateKeyHelper.validateStringParameter("tag type code", tagKey.getTagTypeCode()));
    }
}
