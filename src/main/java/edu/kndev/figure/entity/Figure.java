package edu.kndev.figure.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Figure {
	private int statuscode;
	private List<String> figureCaptions;
	private List<String> figurePath;
}
