/**
 * Copyright (c) 2022 Sebastian Thomschke and others.
 *
 * <p>This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Initial code from https://github.com/microsoft/vscode-textmate/ Initial copyright Copyright
 * (C) Microsoft Corporation. All rights reserved. Initial license: MIT
 *
 * <p>Contributors: - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT
 * license - Sebastian Thomschke - translation and adaptation to Java
 */
package org.eclipse.tm4e.core.registry;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

/**
 * @see <a href=
 *     "https://github.com/microsoft/vscode-textmate/blob/e8d1fc5d04b2fc91384c7a895f6c9ff296a38ac8/src/main.ts#L41">
 *     github.com/microsoft/vscode-textmate/blob/main/src/main.ts</a>
 */
public interface IGrammarConfiguration {

  @Nullable
  default Map<String, Integer> getEmbeddedLanguages() {
    return null;
  }

  @Nullable
  default Map<String, Integer> getTokenTypes() {
    return null;
  }

  @Nullable
  default List<String> getBalancedBracketSelectors() {
    return null;
  }

  @Nullable
  default List<String> getUnbalancedBracketSelectors() {
    return null;
  }
}
