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
package org.finra.herd.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.finra.herd.model.api.xml.TagChild;
import org.finra.herd.model.api.xml.TagKey;
import org.finra.herd.model.jpa.TagEntity;
import org.finra.herd.model.jpa.TagTypeEntity;

public class TagDaoTest extends AbstractDaoTest
{
    @Test
    public void testGetChildrenTags()
    {
        // Create a tag type entity.
        TagTypeEntity tagTypeEntity = tagTypeDaoTestHelper.createTagTypeEntity(TAG_TYPE, TAG_TYPE_DISPLAY_NAME, INTEGER_VALUE);

        // Create two root tag entities for the tag type.
        List<TagEntity> rootTagEntities = Arrays.asList(tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION),
            tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_2, TAG_DISPLAY_NAME_2, TAG_DESCRIPTION));

        // Create two children for the first root tag with tag display name in reverse order.
        List<TagEntity> childrenTagEntities = Arrays
            .asList(tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_3, TAG_DISPLAY_NAME_4, TAG_DESCRIPTION, rootTagEntities.get(0)),
                tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_4, TAG_DISPLAY_NAME_3, TAG_DESCRIPTION, rootTagEntities.get(0)));

        // Create one grand child of the first root tag.
        tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_5, TAG_DISPLAY_NAME_5, TAG_DESCRIPTION, childrenTagEntities.get(0));

        // Get children tags for the first root tag.
        assertEquals(Arrays.asList(childrenTagEntities.get(1), childrenTagEntities.get(0)), tagDao.getChildrenTags(Arrays.asList(rootTagEntities.get(0))));

        // Get children tags for the list of root tags.
        assertEquals(Arrays.asList(childrenTagEntities.get(1), childrenTagEntities.get(0)), tagDao.getChildrenTags(rootTagEntities));

        // Try to get children tags with invalid values for all input parameters (by specifying only the second root tag entity).
        assertTrue(tagDao.getChildrenTags(Arrays.asList(rootTagEntities.get(1))).isEmpty());
    }

    @Test
    public void testGetTagByKey()
    {
        // Create a tag entity.
        TagEntity tagEntity = tagDaoTestHelper.createTagEntity(TAG_TYPE, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION);

        // Get tag entity and validate.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE, TAG_CODE)));

        // Get tag entity by passing all case-insensitive parameters in uppercase.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE.toUpperCase(), TAG_CODE.toUpperCase())));

        // Get tag entity by passing all case-insensitive parameters in lowercase.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE.toLowerCase(), TAG_CODE.toLowerCase())));

        // Try invalid values for all input parameters.
        assertNull(tagDao.getTagByKey(new TagKey("I_DO_NOT_EXIST", TAG_CODE)));
        assertNull(tagDao.getTagByKey(new TagKey(TAG_TYPE, "I_DO_NOT_EXIST")));
    }

    @Test
    public void testGetTagByTagTypeAndDisplayName()
    {
        // Create a tag entity.
        TagEntity tagEntity = tagDaoTestHelper.createTagEntity(TAG_TYPE, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION);

        // Get tag entity and validate.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE, TAG_DISPLAY_NAME));

        // Get tag entity by passing all case-insensitive parameters in uppercase.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE.toUpperCase(), TAG_DISPLAY_NAME.toUpperCase()));

        // Get tag entity by passing all case-insensitive parameters in lowercase.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE.toLowerCase(), TAG_DISPLAY_NAME.toLowerCase()));

        // Try invalid values for all input parameters.
        assertNull(tagDao.getTagByTagTypeAndDisplayName("I_DO_NOT_EXIST", TAG_DISPLAY_NAME));
        assertNull(tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE, "I_DO_NOT_EXIST"));
    }

    @Test
    public void testGetTagsByTagTypeAndParentTagCode()
    {
        // Create a tag type entity.
        TagTypeEntity tagTypeEntity = tagTypeDaoTestHelper.createTagTypeEntity(TAG_TYPE, TAG_TYPE_DISPLAY_NAME, INTEGER_VALUE);

        // Create two root tag entities for the tag type with tag display name in reverse order.
        List<TagEntity> rootTagEntities = Arrays.asList(tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE, TAG_DISPLAY_NAME_2, TAG_DESCRIPTION),
            tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_2, TAG_DISPLAY_NAME, TAG_DESCRIPTION));

        // Create two children for the first root tag with tag display name in reverse order.
        List<TagEntity> childrenTagEntities = Arrays
            .asList(tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_3, TAG_DISPLAY_NAME_4, TAG_DESCRIPTION, rootTagEntities.get(0)),
                tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_4, TAG_DISPLAY_NAME_3, TAG_DESCRIPTION, rootTagEntities.get(0)));

        // Create one grand child of the first root tag.
        tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_5, TAG_DISPLAY_NAME_5, TAG_DESCRIPTION, childrenTagEntities.get(0));

        // Get root tag entities (by not specifying parent tag code).
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_2), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE), HAS_CHILDREN_FLAG_SET)), tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE, null));

        // Get root tag entities by passing all case-insensitive parameters in uppercase.
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_2), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE), HAS_CHILDREN_FLAG_SET)), tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE.toUpperCase(), null));

        // Get root tag entities by passing all case-insensitive parameters in lowercase.
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_2), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE), HAS_CHILDREN_FLAG_SET)), tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE.toLowerCase(), null));

        // Try to get root tags with invalid values for all input parameters.
        assertTrue(tagDao.getTagsByTagTypeAndParentTagCode("I_DO_NOT_EXIST", null).isEmpty());

        // Get children tags (by specifying both tag type and parent tag type code).
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_4), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE_3), HAS_CHILDREN_FLAG_SET)), tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE, TAG_CODE));

        // Get children tags by passing all case-insensitive parameters in uppercase.
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_4), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE_3), HAS_CHILDREN_FLAG_SET)),
            tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE.toUpperCase(), TAG_CODE.toUpperCase()));

        // Get children tags by passing all case-insensitive parameters in lowercase.
        assertEquals(Arrays.asList(new TagChild(new TagKey(TAG_TYPE, TAG_CODE_4), NO_HAS_CHILDREN_FLAG_SET),
            new TagChild(new TagKey(TAG_TYPE, TAG_CODE_3), HAS_CHILDREN_FLAG_SET)),
            tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE.toLowerCase(), TAG_CODE.toLowerCase()));

        // Try to get children tags with invalid values for all input parameters.
        assertTrue(tagDao.getTagsByTagTypeAndParentTagCode("I_DO_NOT_EXIST", TAG_CODE).isEmpty());
        assertTrue(tagDao.getTagsByTagTypeAndParentTagCode(TAG_TYPE, "I_DO_NOT_EXIST").isEmpty());
    }
}
