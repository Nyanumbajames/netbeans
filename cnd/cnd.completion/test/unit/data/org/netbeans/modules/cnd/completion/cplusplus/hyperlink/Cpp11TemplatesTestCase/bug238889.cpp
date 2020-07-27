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

namespace bug238889 {
    namespace ZZZ238889 {
        template <typename T1>
        struct XXX238889 {
            template <typename T2>
            struct BBB238889 {
            };
        };
    }

    template <typename T>
    struct AAA238889 {
        typedef int type;
    };

    int foo238889() {
        AAA238889<ZZZ238889::XXX238889<int>::BBB238889<int>>::type var1;
        AAA238889<ZZZ238889::XXX238889<int>::BBB238889<ZZZ238889::XXX238889<int>>>::type var2;
    } 
}