///*
// * Copyright (C) 2018 acdwisu
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU General Public License
// * as published by the Free Software Foundation; either version 2
// * of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
// */
//package Dao.Mysql;
//
//import Dao.DaoFactory;
//import Dao.DaoTestResult;
//import Dao.DaoTrainResult;
////import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// *
// * @author acdwisu
// */
//public class MysqlDaoFactory extends DaoFactory {
//    
//    private static Connection connection;
//    
//    public static Connection createConnection() {
//        if(connection == null) {
//            MysqlDataSource data = new MysqlDataSource();
//            data.setDatabaseName("swta");
//            data.setUser("root");
//            data.setPassword("");
//            try {
//              connection = data.getConnection();
//            } catch (SQLException ex) {
//              ex.printStackTrace();
//            }
//        }
//        return connection;
//    }
//
//    @Override
//    public DaoTrainResult getDaoTrainResult() {
//        return new MysqlDaoTrainResult();
//    }
//
//    @Override
//    public DaoTestResult getDaoTestResult() {
//        return new MysqlDaoTestResult();
//    }
//}
