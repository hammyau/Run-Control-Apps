#!/usr/bin/env bash
# Copyright Contributors to the GenevaERS Project.
#								SPDX-License-Identifier: Apache-2.0 (c) Copyright IBM Corporation
#								2008
# 
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# 
#   http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
mvn clean
mvn install $1

export rev=`grep "<revision>" pom.xml | awk -F'<revision>||</revision>' '{print $2}'`;
echo RCA release number $rev;

cp RCApps/target/*-jar-with-dependencies.jar $GERS_RCA_JAR_DIR/rcapps-$rev.jar;                                       
cp PerformanceEngine/target/*-jar-with-dependencies.jar $GERS_RCA_JAR_DIR/engine-$rev.jar;                                       
cp ExtractGenerator/target/*-jar-with-dependencies.jar $GERS_RCA_JAR_DIR/extractor-$rev.jar;                                       
                                                                         
cd $GERS_RCA_JAR_DIR;                                                    
                                                                         
touch rcapps-latest.jar;                                                 
rm rcapps-latest.jar;                                                    
ln -s rcapps-$rev.jar rcapps-latest.jar;
touch engine-latest.jar;                                                 
rm engine-latest.jar;                                                    
ln -s engine-$rev.jar engine-latest.jar;
rm extractor-latest.jar;                                                 
touch extractor-latest.jar;                                                 
ln -s extractor-$rev.jar extractor-latest.jar;