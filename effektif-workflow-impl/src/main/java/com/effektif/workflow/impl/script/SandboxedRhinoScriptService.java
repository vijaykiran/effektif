/* Copyright (c) 2014, Effektif GmbH.
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
 * limitations under the License. */
package com.effektif.workflow.impl.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.org.mozilla.javascript.internal.ClassShutter;
import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.ContextFactory;
import sun.org.mozilla.javascript.internal.NativeJavaObject;
import sun.org.mozilla.javascript.internal.Scriptable;
import sun.org.mozilla.javascript.internal.WrapFactory;

import com.effektif.workflow.impl.configuration.Brewable;
import com.effektif.workflow.impl.configuration.Brewery;
import com.effektif.workflow.impl.data.types.TextTypeImpl;
import com.effektif.workflow.impl.workflow.VariableImpl;
import com.effektif.workflow.impl.workflowinstance.VariableInstanceImpl;
import com.effektif.workflow.impl.workflowinstance.WorkflowInstanceImpl;


public class SandboxedRhinoScriptService extends RhinoScriptService implements ScriptService, Brewable {

  private static final Logger log = LoggerFactory.getLogger(SandboxedRhinoScriptService.class);
  
  protected long maxScriptDurationInMillis = 1*1000; // 10 seconds
  
  @Override
  public void brew(Brewery brewery) {
    this.maxScriptDurationInMillis = 1*1000; // 10 seconds
    this.contextFactory = new SandboxContextFactory();
  }

  
  ///////////////////////////////////////////////////
  // FOR TEST
  public static void main(String[] args) {
    String scriptText = "var scriptVar = workflowVar + ' world;";
    SandboxedRhinoScriptService sandboxedRhinoScriptService = new SandboxedRhinoScriptService();
    Script script = sandboxedRhinoScriptService.compile(scriptText);

    WorkflowInstanceImpl workflowInstance = new WorkflowInstanceImpl();
    VariableImpl variable = new VariableImpl();
    variable.id = "existingVar";
    variable.type = new TextTypeImpl();
    VariableInstanceImpl variableInstance = workflowInstance.createVariableInstance(variable);
    variableInstance.value = "hello";

    ScriptResult result = sandboxedRhinoScriptService.evaluate(workflowInstance, script);
  }
  public SandboxedRhinoScriptService() {
    this.contextFactory = new SandboxContextFactory();
    ContextFactory.initGlobal(contextFactory);
  }
  //
  ///////////////////////////////////////////////////

  
  public class SandboxContextFactory extends ContextFactory {
    @Override
    protected Context makeContext() {
      Context context = super.makeContext();
      context.setClassShutter(new SandboxClassShutter());
      context.setWrapFactory(new SandboxWrapFactory());
      context.setOptimizationLevel(-1);
      context.setInstructionObserverThreshold(10);
      return context;
    }
    @Override
    protected void observeInstructionCount(Context context, int instructionCount) {
      SandboxWrapFactory wf = (SandboxWrapFactory) context.getWrapFactory();
      long currentTime = System.currentTimeMillis();
      if (currentTime - wf.start > maxScriptDurationInMillis) {
        throw new Error();
      }
      super.observeInstructionCount(context, instructionCount);
    }
  }
  
  public class SandboxClassShutter implements ClassShutter {
    public boolean visibleToScripts(String className) {
      if (className.startsWith("com.effektif.workflow.impl.script")) {
        return true;
      }
      log.debug("refusing access to " + className);
      return false;
    }
  }

  public static class SandboxWrapFactory extends WrapFactory {
    long start = System.currentTimeMillis();
    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType) {
      return new SandboxNativeJavaObject(scope, javaObject, staticType);
    }
  }

  public static class SandboxNativeJavaObject extends NativeJavaObject {
    public SandboxNativeJavaObject(Scriptable scope, Object javaObject, Class staticType) {
      super(scope, javaObject, staticType);
    }
    @Override
    public Object get(String name, Scriptable start) {
      if (name.equals("getClass")) {
        return NOT_FOUND;
      }
      return super.get(name, start);
    }
  }
}