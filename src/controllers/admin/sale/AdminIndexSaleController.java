package controllers.admin.sale;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import constants.GlobalConstant;
import daos.BookDao;
import daos.SaleOffDao;
import models.Book;
import models.SaleOff;
import models.User;
import utils.CheckUtil;

public class AdminIndexSaleController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AdminIndexSaleController() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User adminLogin = (User) session.getAttribute("adminLogin");
		int idModule = GlobalConstant.SALE_MODULE;
		if (adminLogin.getRole() != 2) {
			if (!CheckUtil.checkRole(adminLogin.getId(), idModule)) {
				response.sendRedirect(request.getContextPath() + "/views/admin/error/error.jsp?err=3");
				return;
			}
		}
		if (request.getParameter("msg") != null) {
			int msg = Integer.parseInt(request.getParameter("msg"));
			if (msg == 1) {
				request.setAttribute("success", "Thêm thành công!");
			} else if (msg == 2) {
				request.setAttribute("success", "Sửa thành công!");
			} else {
				request.setAttribute("success", "Xoá thành công!");
			}
		}
		if (request.getParameter("err") != null) {
			int err = Integer.parseInt(request.getParameter("err"));
			if (err == 1) {
				request.setAttribute("err", "ID không tồn tại!");
			} else {
				request.setAttribute("err", "Có lỗi xảy ra!");
			}
		}
		BookDao objBookDao = new BookDao();
		SaleOffDao objSaleDao = new SaleOffDao();
		int numberPage = GlobalConstant.NUMBER_PAGE2;
		int first = 0, last = 0, pages = 1, totalPages = 1;
		int total = objSaleDao.getCount();
		if (total % numberPage == 0) {
			totalPages = total / numberPage;
		} else {
			totalPages = total / numberPage + 1;
		}
		if (request.getParameter("pages") != null) {
			try {
				pages = Integer.parseInt(request.getParameter("pages"));
				if (pages < 1 || pages > totalPages) {
					response.sendRedirect(request.getContextPath() + "/views/admin/error/error.jsp?err=2");
					return;
				}
			} catch (Exception e) {
				response.sendRedirect(request.getContextPath() + "/views/admin/error/error.jsp?err=1");
				return;
			}
		}
		if (total <= numberPage) {
			first = 0;
			last = total;
		} else {
			first = (pages - 1) * numberPage;
			last = numberPage;
		}
		ArrayList<SaleOff> listSale = objSaleDao.getSale(first, last);
		request.setAttribute("totalPages", totalPages);
		request.setAttribute("total", total);
		request.setAttribute("pages", pages);
		request.setAttribute("first", first);
		request.setAttribute("last", last);
		if (request.getParameter("sname") != null) {
			String sName = request.getParameter("sname");
			request.setAttribute("sname", sName);
			ArrayList<Book> listBook = objBookDao.getBookName(sName);
			listSale = objSaleDao.getSearchSale(listBook);
		}
		request.setAttribute("listSale", listSale);
		RequestDispatcher rd = request.getRequestDispatcher("/views/admin/sale/index.jsp");
		rd.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

}
