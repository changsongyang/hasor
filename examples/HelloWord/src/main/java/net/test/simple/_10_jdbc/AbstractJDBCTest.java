/*
 * Copyright 2008-2009 the original ������(zyc@hasor.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.test.simple._10_jdbc;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoStandardAppContext;
import net.hasor.jdbc.core.JdbcTemplate;
/***
 * ������ɾ�Ĳ����
 * @version : 2014-1-13
 * @author ������(zyc@hasor.net)
 */
public class AbstractJDBCTest {
    private static String config = "net/test/simple/_10_jdbc/jdbc-config.xml";
    protected JdbcTemplate getJdbcTemplate() throws IOException, URISyntaxException {
        AnnoStandardAppContext appContext = new AnnoStandardAppContext(config);
        appContext.start();
        /*���� ���ô洢���� */
        JdbcTemplate jdbc = appContext.getInstance(JdbcTemplate.class);
        /*װ�� SQL �ű��ļ�*/
        jdbc.loadSQL("net/test/simple/_10_jdbc/TB_User.sql");
        jdbc.loadSQL("net/test/simple/_10_jdbc/TB_User_Data.sql");
        return jdbc;
    }
}