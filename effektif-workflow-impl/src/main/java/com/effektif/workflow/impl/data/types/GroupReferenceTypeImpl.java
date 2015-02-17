/*
 * Copyright 2014 Effektif GmbH.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.effektif.workflow.impl.data.types;

import com.effektif.workflow.api.Configuration;
import com.effektif.workflow.api.ref.GroupReference;
import com.effektif.workflow.api.types.GroupReferenceType;
import com.effektif.workflow.impl.data.AbstractDataType;
import com.effektif.workflow.impl.data.DataType;



public class GroupReferenceTypeImpl extends AbstractDataType<GroupReferenceType> {
  
  public GroupReferenceTypeImpl(Configuration configuration) {
    this(new GroupReferenceType(), configuration);
    // TODO initialize the fields
  }

  public GroupReferenceTypeImpl(GroupReferenceType groupReferenceTypeApi, Configuration configuration) {
    super(groupReferenceTypeApi, GroupReference.class);
  }
  
  @Override
  public boolean isStatic() {
    return true;
  }
  
  @Override
  public Object convert(Object value, DataType valueType) {
    if (value instanceof GroupReference) {
      return value;
    }
    if (value instanceof String
         && ( ( valueType==null
                || valueType instanceof TextTypeImpl) 
            )
       ){
      return new GroupReference().id((String)value);
    } 
    throw new RuntimeException("Couldn't convert "+value+" ("+value.getClass().getName()+") to a "+GroupReference.class.getName());
  }
}