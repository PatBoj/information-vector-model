library(ggplot2)
library(shiny)
library(dplyr)

setwd(dirname(rstudioapi::getActiveDocumentContext()$path))

er <- read.table("single files/ising_ER.csv", sep="\t", col.names=c("time", "energy", "distance", "mid", "freq"))
ba <- read.table("single files/ising_BA.csv", sep="\t", col.names=c("time", "energy", "distance", "mid", "freq"))

er$topology <- rep("ER", nrow(er))
ba$topology <- rep("BA", nrow(ba))

data <- rbind(er, ba)

agregaded <- data[data$freq != 0, ] %>%
  group_by(time, distance, topology) %>%
  summarise(mean_corr = sum(freq * mid))
agregaded <- data.frame(agregaded)

distanceHistogram <- function(data) {
  plot <- ggplot(data = data, aes(x = distance, y = mean_corr)) + 
    geom_bar(stat="identity", fill="gray", color="black", width=1) +
    geom_vline(xintercept=0) +
    theme(
      text=element_text(size = 28),
      axis.text=element_text(color= "black"),
      plot.title=element_text(hjust = 0.5),
      panel.border=element_rect(fill = NA),
      panel.background=element_blank(),
      legend.key=element_rect(fill = NA, color = NA),
      legend.background=element_rect(fill = (alpha("white", 0))),
    ) + 
    xlab("distance") +
    ylab("average cosine similarity") +
    scale_x_continuous(limits = c(min(data$distance)-0.55, max(data$distance)+0.55),
                       expand = c(0, 0),
                       sec.axis = dup_axis()) +
    scale_y_continuous(limits = c(-1.05, 1.05),
                       expand = c(0, 0),
                       breaks = seq(-1, 1, 0.2))
  
  plot
}

nonAverage <- function(data) {
  plot <- ggplot(data = data, aes(x = mid, y = freq)) + 
    geom_bar(stat="identity", fill="gray", color="black", width=0.05) +
    geom_hline(yintercept=0) +
    geom_vline(xintercept=0) +
    theme(
      text=element_text(size = 28),
      axis.text=element_text(color= "black"),
      plot.title=element_text(hjust = 0.5),
      panel.border=element_rect(fill = NA),
      panel.background=element_blank(),
      legend.key=element_rect(fill = NA, color = NA),
      legend.background=element_rect(fill = (alpha("white", 0))),
    ) + 
    xlab("cosine similarity") +
    ylab("probability") +
    scale_x_continuous(limits = c(-1.06, 1.06),
                       expand = c(0, 0)) +
    scale_y_continuous(limits = c(0, 1),
                       expand = c(0, 0),
                       breaks = seq(0, 1, 0.1))
  
  plot
}

ui <- fluidPage(
  sidebarLayout(
    sidebarPanel(
      sliderInput("time", "Time", min = 0, max = max(data$time), step = 1000, value = 0),
      radioButtons("topology", label = "topology", choices = list("ER", "BA")), 
      conditionalPanel(condition = "input.tabselected == 1", 
                       uiOutput("distanceSlider")
                       )
    ),
    
    mainPanel(
      tabsetPanel(id="tabselected",
        tabPanel("Distance", value = 1, plotOutput("distance_plot_na", height = 900)),
        tabPanel("Average", value = 2, plotOutput("distance_plot", height = 900))
      )
    )
  )
)

server <- function(input, output) {
  filtered <- reactive({
    agregaded %>%
      filter(time == input$time & topology == input$topology)
  })
  
  output$distance_plot <- renderPlot({
    distanceHistogram(filtered())
  })
  
  filtered_hist <- reactive({
    hist <- data %>%
      filter(time == input$time & distance == input$distance & topology == input$topology)
  })
  
  output$distance_plot_na <- renderPlot({
    nonAverage(filtered_hist())
  })

  output$distanceSlider <- renderUI({
    if (input$topology == "ER") sliderInput("distance", "Distance", min = 0, max(data[data$topology == "ER", 3]), step = 1, value = 0)
    else sliderInput("distance", "Distance", min = 0, max = max(data[data$topology == "BA", 3]), step = 1, value = 0)
  })
}

shinyApp(ui = ui, server = server)
