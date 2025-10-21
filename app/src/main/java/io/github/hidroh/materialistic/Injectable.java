/*
 * Copyright (c) 2015 Ha Duy Trung
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

package io.github.hidroh.materialistic;

import dagger.ObjectGraph;

/**
 * An interface for classes that can be injected with dependencies.
 */
public interface Injectable {
    /**
     * Injects the members of the given object, including injectable members
     * inherited from its supertypes.
     *
     * @param object The object with members to be injected.
     */
    void inject(Object object);

    /**
     * Gets the application's object graph for dependency injection.
     *
     * @return The application's object graph.
     */
    ObjectGraph getApplicationGraph();
}
