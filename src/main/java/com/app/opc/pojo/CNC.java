package com.app.opc.pojo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CNC {
	private String id;
	private LocalDateTime timestamp;
	private float temperature;
	private float cycleTime;
	private int spindleSpeed;
	private int partCount;
	private String status;
}