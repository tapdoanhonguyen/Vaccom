package org.vaccom.vcmgt.controler;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.vaccom.vcmgt.action.ExportDataAction;
import org.vaccom.vcmgt.entity.VaiTro;
import org.vaccom.vcmgt.util.MessageUtil;
import org.vaccom.vcmgt.util.RoleUtil;
import org.vaccom.vcmgt.util.VaccomUtil;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/rest/v1/export")
public class ExportControler {
	@Autowired
	private ExportDataAction exportDataAction;

	@RequestMapping(value = "/nguoitiemchung", method = RequestMethod.POST, produces = "application/octet-stream")
	public ResponseEntity<?> exportNguoiTiemChung(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "cmtcccd", defaultValue = "") String cmtcccd,
			@RequestParam(name = "nhomdoituong", defaultValue = "-1") Integer nhomdoituong,
			@RequestParam("ngaydangki") String ngaydangki, @RequestParam("hovaten") String hovaten,
			@RequestParam(name = "diabancosoid", defaultValue = "-1") Long diabancosoid,
			@RequestParam("cosoytema") String cosoytema,
			@RequestParam(name = "tinhtrangdangky", defaultValue = "-1") Integer tinhtrangdangki,
			@RequestParam(name = "kiemtratrung", defaultValue = "-1") Integer kiemtratrung) {
		try {

			VaiTro vaiTro = (VaiTro) request.getAttribute("_VAI_TRO");
			//TODO check
			
			if (!RoleUtil.isCanBoDiaBan(vaiTro) && !RoleUtil.isCanBoYTe(vaiTro) && !RoleUtil.isQuanTriCoSo(vaiTro) && !RoleUtil.isQuanTriHeThong(vaiTro)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(MessageUtil.getVNMessageText("data.export.permission_error"));
			}

			File file = exportDataAction.exportNguoiTiemChung(cmtcccd, nhomdoituong, ngaydangki, hovaten, diabancosoid,
					cosoytema, tinhtrangdangki, kiemtratrung);

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nguoitiemchung.xls");
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");

			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

			return ResponseEntity.ok().headers(headers).contentLength(file.length())
					.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

		} catch (Exception e) {
			_log.error(e);

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		}
	}

	private Log _log = LogFactory.getLog(ExportControler.class);
}
