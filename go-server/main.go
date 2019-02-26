package main

import (
	"bufio"
	"errors"
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"
)

func findContainerID() (string, error) {
	f, err := os.Open("/proc/1/cpuset")
	if err != nil {
		return "", err
	}
	defer f.Close()
	sc := bufio.NewScanner(f)
	var containerID string
	for sc.Scan() {
		t := sc.Text()
		i := strings.LastIndex(t, "/")
		if i >= 0 {
			containerID = t[i+1:]
			break
		}
		return "", errors.New("Cannot find containerID")
	}
	return containerID, nil
}

func serve() error {
	containerID, err := findContainerID()
	if err != nil {
		return err
	}
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, `{"id": "%s"}`, containerID)
	})
	log.Println("Start localhost:8080")
	return http.ListenAndServe(":8080", nil)
}

func main() {
	log.Fatal(serve())
}

