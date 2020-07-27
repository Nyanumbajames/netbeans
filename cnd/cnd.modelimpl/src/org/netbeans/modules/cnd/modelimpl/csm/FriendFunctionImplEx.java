/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.netbeans.modules.cnd.modelimpl.csm;

import java.io.IOException;
import org.netbeans.modules.cnd.antlr.collections.AST;
import org.netbeans.modules.cnd.api.model.CsmClass;
import org.netbeans.modules.cnd.api.model.CsmDeclaration.Kind;
import org.netbeans.modules.cnd.api.model.CsmFile;
import org.netbeans.modules.cnd.api.model.CsmFriendFunction;
import org.netbeans.modules.cnd.api.model.CsmFunction;
import org.netbeans.modules.cnd.api.model.CsmScope;
import org.netbeans.modules.cnd.api.model.CsmUID;
import org.netbeans.modules.cnd.api.model.util.CsmKindUtilities;
import org.netbeans.modules.cnd.modelimpl.content.file.FileContent;
import org.netbeans.modules.cnd.modelimpl.csm.core.AstRenderer;
import org.netbeans.modules.cnd.modelimpl.csm.core.FileImpl;
import org.netbeans.modules.cnd.modelimpl.textcache.NameCache;
import org.netbeans.modules.cnd.modelimpl.textcache.QualifiedNameCache;
import org.netbeans.modules.cnd.modelimpl.uid.UIDCsmConverter;
import org.netbeans.modules.cnd.modelimpl.uid.UIDObjectFactory;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataInput;
import org.netbeans.modules.cnd.repository.spi.RepositoryDataOutput;

/**
 *
 */
public final class FriendFunctionImplEx extends FunctionImplEx<CsmFriendFunction> implements CsmFriendFunction {
    private final CsmUID<CsmClass> friendClassUID;
    
    protected FriendFunctionImplEx(CharSequence name, CharSequence rawName, CsmScope scope, CsmClass cls, boolean _static,  FunctionImpl.CV_RL _const, CsmFile file, int startOffset, int endOffset, boolean global) {
        super(name, rawName, scope, _static, _const, file, startOffset, endOffset, global);
        friendClassUID = UIDCsmConverter.declarationToUID(cls);
    }

    public static FriendFunctionImplEx create(AST ast, final CsmFile file, FileContent fileContent, CsmClass cls, CsmScope scope, boolean global) throws AstRendererException {
        
        int startOffset = getStartOffset(ast);
        int endOffset = getEndOffset(ast);
        
        NameHolder nameHolder = NameHolder.createFunctionName(ast);
        CharSequence name = QualifiedNameCache.getManager().getString(nameHolder.getName());
        if (name.length() == 0) {
            AstRendererException.throwAstRendererException((FileImpl) file, ast, startOffset, "Empty function name."); // NOI18N
        }
        CharSequence rawName = initRawName(ast);
        
        boolean _static = AstRenderer.FunctionRenderer.isStatic(ast, file, fileContent, name);
        FunctionImpl.CV_RL _const = AstRenderer.FunctionRenderer.isConst(ast);

        scope = AstRenderer.FunctionRenderer.getScope(scope, file, _static, false);

        FriendFunctionImplEx friendFunctionImplEx = new FriendFunctionImplEx(name, rawName, scope, cls, _static, _const, file, startOffset, endOffset, global);        
        
        temporaryRepositoryRegistration(ast, global, friendFunctionImplEx);
        
        StringBuilder clsTemplateSuffix = new StringBuilder();
        TemplateDescriptor templateDescriptor = createTemplateDescriptor(ast, file, friendFunctionImplEx, clsTemplateSuffix, global);
        CharSequence classTemplateSuffix = NameCache.getManager().getString(clsTemplateSuffix);
        
        friendFunctionImplEx.setTemplateDescriptor(templateDescriptor, classTemplateSuffix);
        friendFunctionImplEx.setReturnType(AstRenderer.FunctionRenderer.createReturnType(ast, friendFunctionImplEx, file));
        friendFunctionImplEx.setParameters(AstRenderer.FunctionRenderer.createParameters(ast, friendFunctionImplEx, file, fileContent), 
                AstRenderer.FunctionRenderer.isVoidParameter(ast));        
        
        CharSequence[] classOrNspNames = CastUtils.isCast(ast) ?
            getClassOrNspNames(ast) :
            friendFunctionImplEx.initClassOrNspNames(ast);
        friendFunctionImplEx.setClassOrNspNames(classOrNspNames);        
        
        postObjectCreateRegistration(global, friendFunctionImplEx);
        postFunctionImpExCreateRegistration(fileContent, global, friendFunctionImplEx);
        nameHolder.addReference(fileContent, friendFunctionImplEx);
        return friendFunctionImplEx;
    }
    
    @Override
    public CsmFunction getReferencedFunction() {
        CsmFunction fun = this;
        if (CsmKindUtilities.isFunctionDeclaration(this)){
            fun = getDefinition();
            if (fun == null){
                fun = this;
            }
        }
        return fun;
    }
    
    @Override
    public CsmClass getContainingClass() {
        CsmClass cls = UIDCsmConverter.UIDtoClass(friendClassUID);
        assert (friendClassUID != null) : "null object for UID " + friendClassUID;
        return cls;
    }

    @Override
    public Kind getKind() {
        return Kind.FUNCTION_FRIEND;
    }

    public static class FriendFunctionExBuilder extends FunctionExBuilder {
    
        @Override
        public CsmScope getScope() {
            CsmScope scope = super.getScope();
            while (CsmKindUtilities.isClass(scope)) {
               CsmScope newScope = ((CsmClass)scope).getScope(); 
               if (newScope != null) {
                   scope = newScope;
               } else {
                   break;
               }
            }
            return AstRenderer.FunctionRenderer.getScope(scope, getFile(), isStatic(), false);
        } 
        
        public CsmClass getCls() {
            return (CsmClass) super.getScope();
        } 
        
        @Override
        public FriendFunctionImplEx create() {
            FriendFunctionImplEx fun = new FriendFunctionImplEx(getName(), getRawName(), getScope(), getCls(), isStatic(), FunctionImpl.CV_RL.isConst(isConst()), getFile(), getStartOffset(), getEndOffset(), isGlobal());
            init(fun);
            return fun;
        }
    }    
    
    @Override
    public void write(RepositoryDataOutput output) throws IOException {
        super.write(output);
        UIDObjectFactory.getDefaultFactory().writeUID(friendClassUID, output);
    }
    
    public FriendFunctionImplEx(RepositoryDataInput input) throws IOException {
        super(input);
        friendClassUID = UIDObjectFactory.getDefaultFactory().readUID(input);
    }       
}
