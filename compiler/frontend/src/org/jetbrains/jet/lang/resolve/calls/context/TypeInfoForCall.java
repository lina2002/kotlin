/*
 * Copyright 2010-2013 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.resolve.calls.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.FunctionDescriptor;
import org.jetbrains.jet.lang.diagnostics.Diagnostic;
import org.jetbrains.jet.lang.psi.Call;
import org.jetbrains.jet.lang.resolve.BindingContext;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.BindingTraceContext;
import org.jetbrains.jet.lang.resolve.calls.autocasts.DataFlowInfo;
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCall;
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCallImpl;
import org.jetbrains.jet.lang.resolve.calls.tasks.TracingStrategy;
import org.jetbrains.jet.lang.types.JetType;
import org.jetbrains.jet.lang.types.JetTypeInfo;
import org.jetbrains.jet.lang.types.TypeUtils;
import org.jetbrains.jet.util.slicedmap.ReadOnlySlice;
import org.jetbrains.jet.util.slicedmap.WritableSlice;

import java.util.Collection;

public class TypeInfoForCall {

    private final static BindingTrace TRACE_STUB = new BindingTrace() {

        @Override
        public BindingContext getBindingContext() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K, V> void record(WritableSlice<K, V> slice, K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <K> void record(WritableSlice<K, Boolean> slice, K key) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public <K, V> V get(ReadOnlySlice<K, V> slice, K key) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public <K, V> Collection<K> getKeys(WritableSlice<K, V> slice) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void report(@NotNull Diagnostic diagnostic) {
            throw new UnsupportedOperationException();
        }
    };

    private final JetTypeInfo typeInfo;

    private final CallCandidateResolutionContext<FunctionDescriptor> callCandidateResolutionContext;


    @NotNull
    public static TypeInfoForCall create(@Nullable JetType type, @NotNull DataFlowInfo dataFlowInfo) {
        return create(JetTypeInfo.create(type, dataFlowInfo));
    }

    @NotNull
    public static TypeInfoForCall create(
            @Nullable JetType type,
            @NotNull DataFlowInfo dataFlowInfo,
            @NotNull ResolvedCall<FunctionDescriptor> resolvedCall,
            @NotNull Call call,
            @NotNull ResolutionContext context,
            @NotNull ResolveMode resolveMode
    ) {
        JetTypeInfo typeInfo = JetTypeInfo.create(type, dataFlowInfo);
        CallCandidateResolutionContext<FunctionDescriptor> callCandidateResolutionContext;
        if (resolvedCall instanceof ResolvedCallImpl) {
            //todo[ResolvedCallImpl]
            callCandidateResolutionContext = CallCandidateResolutionContext.createForCallBeingAnalyzed(
                    (ResolvedCallImpl<FunctionDescriptor>) resolvedCall, context.replaceBindingTrace(TRACE_STUB),
                    call, resolveMode, ((ResolvedCallImpl<FunctionDescriptor>) resolvedCall).getTracing());
        }
        else {
            callCandidateResolutionContext = null;
        }
        return new TypeInfoForCall(typeInfo, callCandidateResolutionContext);

    }

    @NotNull
    public static TypeInfoForCall create(@NotNull JetTypeInfo typeInfo) {
        return new TypeInfoForCall(typeInfo, null);
    }

    @NotNull
    public static TypeInfoForCall create(
            @NotNull JetTypeInfo typeInfo,
            @NotNull TypeInfoForCall typeInfoForCall
    ) {
        return new TypeInfoForCall(typeInfo, typeInfoForCall.getCallCandidateResolutionContext());
    }

    @NotNull
    public static TypeInfoForCall create(
            @NotNull JetTypeInfo typeInfo,
            @Nullable CallCandidateResolutionContext<FunctionDescriptor> callCandidateResolutionContext
    ) {
        return new TypeInfoForCall(typeInfo, callCandidateResolutionContext);
    }

    private TypeInfoForCall(
            @NotNull JetTypeInfo typeInfo,
            @Nullable CallCandidateResolutionContext<FunctionDescriptor> callCandidateResolutionContext
    ) {
        this.typeInfo = typeInfo;
        this.callCandidateResolutionContext = callCandidateResolutionContext;
    }

    @Nullable
    public CallCandidateResolutionContext<FunctionDescriptor> getCallCandidateResolutionContext() {
        return callCandidateResolutionContext;
    }

    @NotNull
    public JetTypeInfo getTypeInfo() {
        return typeInfo;
    }

    @Nullable
    public JetType getType() {
        return typeInfo.getType();
    }

    @NotNull
    public DataFlowInfo getDataFlowInfo() {
        return typeInfo.getDataFlowInfo();
    }

}
