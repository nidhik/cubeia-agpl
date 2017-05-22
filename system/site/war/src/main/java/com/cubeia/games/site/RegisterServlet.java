/**
 * Copyright (C) 2014 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.site;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
        
import com.cubeia.games.poker.CreateUser;
import com.sampullara.cli.Args;


@WebServlet("/register")
public class RegisterServlet extends HttpServlet
{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {

        PrintWriter out = resp.getWriter();
        
        String name = req.getParameter("username");
        String password = req.getParameter("password");   
        
        CreateUser creator = new CreateUser();
        String[] args = {"-d", "-u", name, "-p", password, "-o", "1"};       
        
        try {
            Args.parse(creator, args);
            creator.execute();
            
            out.println("<html>");
            out.println("<body>");
            out.println("You can play now <br/>");
            out.println("After login, click top-right to see your account</br>");
            out.println("<a href=\"game\">Enter the Game</a>");
            out.println("</body>");
            out.println("</html>");            
        }
        catch(IllegalArgumentException e) {
            Args.usage(creator);
        }
        catch(Exception e) {
            out.println("!!! User Registration Failed !!!");
            out.println("Please go back and try again.");
            out.println(e);
            e.printStackTrace(out);
        }

    }    
}